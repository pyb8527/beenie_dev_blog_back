package com.beenie.backend.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    @Value("${app.cookie-secure:false}")
    private boolean secure;

    /**
     * 쿠키 도메인. 비어 있으면 host-only(로컬 개발). 서브도메인(blog./api.)으로 배포할 때는
     * {@code .weenie-beenie.net}처럼 상위 도메인을 지정해 두 서브도메인이 인증 쿠키를 공유하게 한다.
     */
    @Value("${app.cookie-domain:}")
    private String cookieDomain;

    public static final String ACCESS_TOKEN_COOKIE = "access_token";
    public static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    public ResponseCookie build(String name, String value, long maxAgeSeconds) {
        return base(name, value, maxAgeSeconds).build();
    }

    public ResponseCookie expire(String name) {
        return base(name, "", 0).build();
    }

    private ResponseCookie.ResponseCookieBuilder base(String name, String value, long maxAgeSeconds) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("Lax");
        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }
        return builder;
    }
}
