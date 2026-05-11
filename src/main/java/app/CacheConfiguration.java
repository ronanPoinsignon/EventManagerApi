package app;

import app.back.service.KeycloakUserService;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableCaching
@EnableScheduling
public class CacheConfiguration {

    private Logger logger = LoggerFactory.getLogger(CacheConfiguration.class);

    private static final int CACHE_DURATION = 5;
    private static final int CACHE_SIZE = 10000;

    private final KeycloakUserService keycloakUserService;

    public CacheConfiguration(KeycloakUserService keycloakUserService) {
        this.keycloakUserService = keycloakUserService;
    }

    @Bean
    public CacheManager keycloakUserCache() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("keycloak_users");

        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(CACHE_DURATION))
                        .maximumSize(CACHE_SIZE)
        );

        return cacheManager;
    }

    @Scheduled(fixedRate = CACHE_DURATION, timeUnit = TimeUnit.MINUTES)
    public void warmCache() {
        logger.info("Update du cache des utilisateurs Keycloak.");
        var knownUsers = keycloakUserService.getUsers();

        knownUsers.forEach(user -> {
            try {
                keycloakUserService.getUserById(user.getId());
            } catch (Exception ignored) {}
        });
    }

}
