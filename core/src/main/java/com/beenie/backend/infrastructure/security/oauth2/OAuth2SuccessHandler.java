package com.beenie.backend.infrastructure.security.oauth2;

import com.beenie.backend.domain.auth.RefreshTokenRepository;
import com.beenie.backend.infrastructure.security.CookieUtil;
import com.beenie.backend.infrastructure.security.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtil cookieUtil;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Long userId = ((Number) oAuth2User.getAttributes().get(CustomOAuth2UserService.ATTR_INTERNAL_USER_ID)).longValue();
        String role = (String) oAuth2User.getAttributes().get(CustomOAuth2UserService.ATTR_INTERNAL_ROLE);

        String accessToken = jwtTokenProvider.createAccessToken(userId, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        refreshTokenRepository.save(userId, refreshToken, jwtTokenProvider.getRefreshTokenExpirationSeconds());

        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.build(CookieUtil.ACCESS_TOKEN_COOKIE, accessToken,
                        jwtTokenProvider.getAccessTokenExpirationSeconds()).toString());
        response.addHeader(HttpHeaders.SET_COOKIE,
                cookieUtil.build(CookieUtil.REFRESH_TOKEN_COOKIE, refreshToken,
                        jwtTokenProvider.getRefreshTokenExpirationSeconds()).toString());

        response.sendRedirect(frontendUrl + "/login");
    }
}
