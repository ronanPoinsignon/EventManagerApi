package app.back.service;

import app.back.dto.KeycloakUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class KeycloakUserService {

    @Value("${KEYCLOAK_REALM}")
    private String keycloakRealmValue;

    @Value("${KEYCLOAK_PORT}")
    private int keycloakPort;

    @Value("${KEYCLOAK_CLIENT_ID}")
    private String clientId;

    @Value("${KEYCLOAK_USER}")
    private String keycloakAdminUser;
    @Value("${KEYCLOAK_PASSWORD}")
    private String keycloakAdminPassword;

    private final ObjectMapper mapper;
    private final RestTemplate restTemplate;

    private String clientSecret;

    public KeycloakUserService(ObjectMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
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

        var response = restTemplate.exchange(route, HttpMethod.GET, entity, String.class);
        var body = response.getBody();

        var root = mapper.readTree(body);
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

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        return response.getBody().get("access_token").toString();
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

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

        return response.getBody().get("access_token").toString();
    }

    public List<KeycloakUser> getUsers() {
        var route = getBaseURL() + "/admin/realms/" + keycloakRealmValue + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + getToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        var response = restTemplate.exchange(route, HttpMethod.GET, entity, KeycloakUser[].class);
        assert response.getBody() != null;
        return Arrays.asList(response.getBody());
    }

    private String getBaseURL() {
        return "http://keycloak:" + keycloakPort;
    }

    private String getRealmURL() {
        return getBaseURL() + "/realms/" + keycloakRealmValue;
    }

    private String getRealmUsersURL() {
        return getRealmURL() + "/users";
    }

}