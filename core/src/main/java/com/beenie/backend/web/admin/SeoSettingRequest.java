package com.beenie.backend.web.admin;

import jakarta.validation.constraints.NotBlank;

public record SeoSettingRequest(
        @NotBlank(message = "블로그 제목은 필수입니다.")
        String blogTitle,
        String blogSubtitle,
        String blogDescription,
        String profileImageUrl,
        String metaDescription,
        String keywords,
        String robotsTxt,
        String gaId,
        String gscVerification
) {
}
