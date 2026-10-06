package com.workouttracker.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilsTest {

    private static final String SECRET = base64Key('a');
    private static final String OTHER_SECRET = base64Key('b');

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = jwtUtils(SECRET, 60_000);
    }

    @Test
    void generatedTokenIsValidAndCarriesUsername() {
        String token = jwtUtils.generateJwtToken("alice");

        assertThat(jwtUtils.validateJwtToken(token)).isTrue();
        assertThat(jwtUtils.getUserNameFromJwtToken(token)).isEqualTo("alice");
    }

    @Test
    void rejectsTokenSignedWithDifferentKey() {
        String forged = jwtUtils(OTHER_SECRET, 60_000).generateJwtToken("alice");

        assertThat(jwtUtils.validateJwtToken(forged)).isFalse();
    }

    @Test
    void rejectsTamperedToken() {
        String token = jwtUtils.generateJwtToken("alice");
        String[] parts = token.split("\\.");
        String otherPayload = jwtUtils.generateJwtToken("mallory").split("\\.")[1];

        assertThat(jwtUtils.validateJwtToken(parts[0] + "." + otherPayload + "." + parts[2])).isFalse();
    }

    @Test
    void rejectsExpiredToken() {
        String expired = jwtUtils(SECRET, -1_000).generateJwtToken("alice");

        assertThat(jwtUtils.validateJwtToken(expired)).isFalse();
    }

    @Test
    void rejectsMalformedAndEmptyTokens() {
        assertThat(jwtUtils.validateJwtToken("not-a-jwt")).isFalse();
        assertThat(jwtUtils.validateJwtToken("")).isFalse();
    }

    private static JwtUtils jwtUtils(String secret, int expirationMs) {
        JwtUtils utils = new JwtUtils();
        ReflectionTestUtils.setField(utils, "jwtSecret", secret);
        ReflectionTestUtils.setField(utils, "jwtExpirationMs", expirationMs);
        return utils;
    }

    private static String base64Key(char fill) {
        return Base64.getEncoder().encodeToString(String.valueOf(fill).repeat(64).getBytes());
    }
}
