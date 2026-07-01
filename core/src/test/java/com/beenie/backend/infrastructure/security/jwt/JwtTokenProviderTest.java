package com.beenie.backend.infrastructure.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", "test-secret-key-must-be-long-enough-for-hs256-signing");
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpirationMillis", 1_800_000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenExpirationMillis", 1_209_600_000L);
        ReflectionTestUtils.invokeMethod(jwtTokenProvider, "init");
    }

    @Test
    void issuesAccessTokenThatValidatesAndCarriesUserIdAndRole() {
        String token = jwtTokenProvider.createAccessToken(42L, "ADMIN");

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserId(token)).isEqualTo(42L);
        assertThat(jwtTokenProvider.getRole(token)).isEqualTo("ADMIN");
        assertThat(jwtTokenProvider.getType(token)).isEqualTo("access");
    }

    @Test
    void issuesRefreshTokenWithoutRoleClaim() {
        String token = jwtTokenProvider.createRefreshToken(7L);

        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserId(token)).isEqualTo(7L);
        assertThat(jwtTokenProvider.getType(token)).isEqualTo("refresh");
        assertThat(jwtTokenProvider.getRole(token)).isNull();
    }

    @Test
    void rejectsTamperedToken() {
        String token = jwtTokenProvider.createAccessToken(1L, "USER");
        String tampered = token.substring(0, token.length() - 2) + "xx";

        assertThat(jwtTokenProvider.validateToken(tampered)).isFalse();
    }

    @Test
    void rejectsExpiredToken() {
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenExpirationMillis", -1000L);
        String token = jwtTokenProvider.createAccessToken(1L, "USER");

        assertThat(jwtTokenProvider.validateToken(token)).isFalse();
        assertThat(jwtTokenProvider.isExpired(token)).isTrue();
    }

    @Test
    void rejectsMalformedToken() {
        assertThat(jwtTokenProvider.validateToken("not-a-jwt")).isFalse();
    }
}
