package com.beenie.backend.web.auth;

import com.beenie.backend.application.auth.AuthResult;
import com.beenie.backend.application.auth.AuthService;
import com.beenie.backend.domain.user.User;
import com.beenie.backend.infrastructure.security.AuthPrincipal;
import com.beenie.backend.infrastructure.security.CookieUtil;
import com.beenie.backend.infrastructure.security.jwt.JwtTokenProvider;
import com.beenie.backend.support.common.response.ApiResponse;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Void>> refresh(HttpServletRequest request) {
        String refreshToken = extractCookie(request, CookieUtil.REFRESH_TOKEN_COOKIE);
        AuthResult result = authService.refresh(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.build(CookieUtil.ACCESS_TOKEN_COOKIE,
                        result.accessToken(), jwtTokenProvider.getAccessTokenExpirationSeconds()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieUtil.build(CookieUtil.REFRESH_TOKEN_COOKIE,
                        result.refreshToken(), jwtTokenProvider.getRefreshTokenExpirationSeconds()).toString())
                .body(ApiResponse.empty());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal AuthPrincipal principal) {
        if (principal != null) {
            authService.logout(principal.userId());
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.expire(CookieUtil.ACCESS_TOKEN_COOKIE).toString())
                .header(HttpHeaders.SET_COOKIE, cookieUtil.expire(CookieUtil.REFRESH_TOKEN_COOKIE).toString())
                .body(ApiResponse.empty());
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal AuthPrincipal principal) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        User user = authService.me(principal.userId());
        return ApiResponse.success(UserResponse.from(user));
    }

    private String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
