package com.beenie.backend.web.admin;

import com.beenie.backend.domain.setting.SiteSetting;

public record SeoSettingResponse(
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
    public static SeoSettingResponse from(SiteSetting setting) {
        return new SeoSettingResponse(setting.getBlogTitle(), setting.getBlogSubtitle(), setting.getBlogDescription(),
                setting.getProfileImageUrl(), setting.getMetaDescription(), setting.getKeywords(),
                setting.getRobotsTxt(), setting.getGaId(), setting.getGscVerification());
    }
}
