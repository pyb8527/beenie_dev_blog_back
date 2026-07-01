package com.beenie.backend.application.auth;

import com.beenie.backend.domain.auth.RefreshTokenRepository;
import com.beenie.backend.domain.user.User;
import com.beenie.backend.domain.user.UserRepository;
import com.beenie.backend.infrastructure.security.jwt.JwtTokenProvider;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    /**
     * Refresh Token Rotation(RTR): 제출된 토큰이 저장된 최신 토큰과 다르면 재사용으로 간주하고
     * 모든 세션(저장된 토큰)을 무효화한다.
     */
    public AuthResult refresh(String providedRefreshToken) {
        if (providedRefreshToken == null || !jwtTokenProvider.validateToken(providedRefreshToken)
                || !"refresh".equals(jwtTokenProvider.getType(providedRefreshToken))) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserId(providedRefreshToken);
        String storedToken = refreshTokenRepository.find(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!storedToken.equals(providedRefreshToken)) {
            // 이미 사용된(회전된) 토큰의 재사용 시도 -> 탈취 의심, 세션 전체 무효화
            refreshTokenRepository.delete(userId);
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_REUSED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        refreshTokenRepository.save(user.getId(), newRefreshToken, jwtTokenProvider.getRefreshTokenExpirationSeconds());

        return new AuthResult(user.getId(), user.getRole().name(), newAccessToken, newRefreshToken);
    }

    public void logout(Long userId) {
        if (userId != null) {
            refreshTokenRepository.delete(userId);
        }
    }

    public User me(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
