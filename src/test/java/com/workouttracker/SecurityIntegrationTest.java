package com.workouttracker;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs against a real servlet container so Spring's /error handling and the
 * startup admin bootstrap behave exactly as in production.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "jwt.secret=dGVzdC1vbmx5LXNlY3JldC10ZXN0LW9ubHktc2VjcmV0LXRlc3Qtb25seS1zZWNyZXQtdGVzdC1vbmx5LXNlY3JldA==",
        "app.admin.username=rootadmin",
        "app.admin.email=root@example.com",
        "app.admin.password=correct-horse-battery",
        "spring.datasource.url=jdbc:h2:mem:security_it;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
})
class SecurityIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void invalidLoginBodyIsBadRequestNotUnauthorized() {
        ResponseEntity<String> response = rest.postForEntity("/auth/login",
                json(Map.of("password", "x")), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void userListIsNotPublic() {
        ResponseEntity<String> response = rest.getForEntity("/users", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void regularUserIsForbiddenFromAdminEndpoints() {
        rest.postForEntity("/auth/register", json(Map.of(
                "username", "plainuser", "email", "plain@example.com", "password", "secret123")), String.class);
        String token = login("plainuser", "secret123");

        assertThat(get("/users", token).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(get("/reports/user/1?startDate=2026-01-01T00:00:00&endDate=2026-01-31T00:00:00", token)
                .getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void bootstrappedAdminCanUseAdminAndUserEndpoints() {
        String token = login("rootadmin", "correct-horse-battery");

        assertThat(get("/users", token).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get("/reports/user/1?startDate=2026-01-01T00:00:00&endDate=2026-01-31T00:00:00", token)
                .getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get("/reports/weekly", token).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String login(String username, String password) {
        ResponseEntity<JsonNode> response = rest.postForEntity("/auth/login",
                json(Map.of("usernameOrEmail", username, "password", password)), JsonNode.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody().get("accessToken").asText();
    }

    private ResponseEntity<String> get(String url, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return rest.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
    }

    private static HttpEntity<Map<String, String>> json(Map<String, String> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
