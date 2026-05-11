package app.back.service;

import app.RestTemplateConfiguration;
import app.back.api.KeycloakServiceApi;
import app.back.dto.KeycloakUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class KeycloakUserService implements KeycloakServiceApi {

    @Value("${KEYCLOAK_REALM}")
    private String keycloakRealmValue;

    @Value("${keycloak.server-url}")
    private String keycloakBaseUrl;

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String clientId;

    @Value("${KEYCLOAK_USER}")
    private String keycloakAdminUser;
    @Value("${KEYCLOAK_PASSWORD}")
    private String keycloakAdminPassword;

    private final ObjectMapper mapper;
    private final RestTemplateConfiguration.WebRequester webRequester;

    private String clientSecret;

    public KeycloakUserService(ObjectMapper mapper, RestTemplateConfiguration.WebRequester webRequester) {
        this.mapper = mapper;
        this.webRequester = webRequester;
    }

    @PostConstruct
    private void init() {
        clientSecret = getClientSecret();
    }

    private String getClientSecret() {
        var accessToken = getAdminToken();
        var route = getBaseURL() + "/admin/realms/event_organizer/clients?clientId=" + clientId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        var response = webRequester.get(route, entity, String.class);

        var root = mapper.readTree(response);
        var result = root.get(0);
        return result.get("secret").asString();
    }

    private String getAdminToken() {
        var url = getBaseURL() + "/realms/master/protocol/openid-connect/token";
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("username", keycloakAdminUser);
        body.add("password", keycloakAdminPassword);
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        var response = webRequester.post(url, request, new ParameterizedTypeReference<Map<String, String>>() {
        });

        return response.get("access_token");
    }

    private String getToken() {
        var url = getRealmURL() + "/protocol/openid-connect/token";
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        var response = webRequester.<Map<String, String>>post(url, request, new ParameterizedTypeReference<>() {
        });

        return response.get("access_token");
    }

    @Override
    public List<KeycloakUser> getUsers() {
        var route = getBaseURL() + "/admin/realms/" + keycloakRealmValue + "/users";
        return requestWithToken(route, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
    }

    @Cacheable(cacheNames="keycloak_users", cacheManager="keycloakUserCache", key = "#userId.toString()", sync = true)
    @Override
    public Optional<KeycloakUser> getUserById(UUID userId) {
        var route = getBaseURL() + "/admin/realms/" + keycloakRealmValue + "/users/" + userId;
        var result = requestWithToken(route, HttpMethod.GET, null, new ParameterizedTypeReference<KeycloakUser>() {});
        return Optional.ofNullable(result);
    }

    private <T> T requestWithToken(String route, HttpMethod method, MultiValueMap<String, String> body, ParameterizedTypeReference<T> type) {
        var headers = getTokenHeaders();
        var entity = new HttpEntity<>(body, headers);
        return webRequester.request(route, method, entity, type);
    }

    private HttpHeaders getTokenHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getToken());

        return headers;
    }

    private String getBaseURL() {
        return keycloakBaseUrl;
    }

    private String getRealmURL() {
        return getBaseURL() + "/realms/" + keycloakRealmValue;
    }

    private String getRealmUsersURL() {
        return getRealmURL() + "/users";
    }

}