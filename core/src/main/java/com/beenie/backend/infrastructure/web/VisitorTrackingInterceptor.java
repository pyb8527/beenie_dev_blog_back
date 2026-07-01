package com.beenie.backend.infrastructure.web;

import com.beenie.backend.domain.stats.VisitorStatsRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class VisitorTrackingInterceptor implements HandlerInterceptor {

    private final VisitorStatsRepository visitorStatsRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String ip = resolveIp(request);
            String ua = request.getHeader("User-Agent");
            visitorStatsRepository.recordVisit(hash(ip + "|" + ua));
        } catch (Exception ignored) {
            // 방문자 집계 실패는 요청 처리에 영향을 주지 않는다.
        }
        return true;
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String hash(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
