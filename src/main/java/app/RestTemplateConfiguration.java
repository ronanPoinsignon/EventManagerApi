package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.function.Supplier;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public WebRequester restTemplate() {
        return new WebRequester(new RestTemplate());
    }

    public static class WebRequester {

        Logger logger = LoggerFactory.getLogger(WebRequester.class);

        private final RestTemplate restTemplate;

        public WebRequester(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        public <T> T request(String route, HttpMethod httpMethod, HttpEntity<?> header, Class<T> response) {
            return handleRequest(() -> restTemplate.exchange(route, httpMethod, header, response));
        }

        public <T> T request(String route, HttpMethod httpMethod, HttpEntity<?> header, ParameterizedTypeReference<T> parameterizedTypeReference) {
            return handleRequest(() -> restTemplate.exchange(route, httpMethod, header, parameterizedTypeReference));
        }

        private <T> T handleRequest(Supplier<ResponseEntity<T>> requestSupplier) {
            try {
                var result = requestSupplier.get();
                return result.getBody();
            } catch(ResourceAccessException e) {
                logger.error(e.getMessage(), e);
                throw new ResourceAccessException("Impossible de se connecter à Keycloak.");
            } catch(HttpClientErrorException.NotFound e) {
                return null;
            } catch(HttpClientErrorException e) {
                logger.error(e.getMessage(), e);
                throw e;
            }
        }

        public <T> T post(String route, HttpEntity<?> header, Class<T> response) {
            return this.request(route, HttpMethod.POST, header, response);
        }

        public <T> T post(String route, HttpEntity<?> header, ParameterizedTypeReference<T> parameterizedTypeReference) {
            return this.request(route, HttpMethod.POST, header, parameterizedTypeReference);
        }

        public <T> T get(String route, HttpEntity<?> header, Class<T> response) {
            return this.request(route, HttpMethod.GET, header, response);
        }

        public <T> T get(String route, HttpEntity<?> header, ParameterizedTypeReference<T> parameterizedTypeReference) {
            return this.request(route, HttpMethod.GET, header, parameterizedTypeReference);
        }
    }
}
