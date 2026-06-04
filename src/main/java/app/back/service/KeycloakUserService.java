package app.back.service;

import app.RestTemplateConfiguration;
import app.back.api.KeycloakUserServiceApi;
import app.back.dto.KeycloakUser;
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

import java.util.*;

@Service
public class KeycloakUserService implements KeycloakUserServiceApi {

    @Value("${KEYCLOAK_REALM}")
    private String keycloakRealmValue;

    @Value("${keycloak.internal-server-url}")
    private String keycloakInternalBaseUrl;

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String clientId;
    @Value("${KEYCLOAK_CLIENT_SECRET}")
    private String clientSecret;

    private final RestTemplateConfiguration.WebRequester webRequester;

    public KeycloakUserService(RestTemplateConfiguration.WebRequester webRequester) {
        this.webRequester = webRequester;
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

    @Override
    public LinkedHashMap<String, String> impersonate(String keycloakUserId) {
        var url = getRealmURL() + "/protocol/openid-connect/token";
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("subject_token", getToken());
        body.add("requested_subject", keycloakUserId);
        body.add("requested_token_type", "urn:ietf:params:oauth:token-type:access_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return webRequester.post(url, request, new ParameterizedTypeReference<>() {});
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
        System.out.println("BASE URL : " + keycloakInternalBaseUrl);
        return keycloakInternalBaseUrl;
    }

    private String getRealmURL() {
        return getBaseURL() + "/realms/" + keycloakRealmValue;
    }

    private String getRealmUsersURL() {
        return getRealmURL() + "/users";
    }

}