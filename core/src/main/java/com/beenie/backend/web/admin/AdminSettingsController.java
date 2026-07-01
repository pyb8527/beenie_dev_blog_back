package com.beenie.backend.web.admin;

import com.beenie.backend.application.admin.SettingsService;
import com.beenie.backend.domain.setting.SiteSetting;
import com.beenie.backend.support.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/settings/seo")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public ApiResponse<SeoSettingResponse> get() {
        return ApiResponse.success(SeoSettingResponse.from(settingsService.get()));
    }

    @PutMapping
    public ApiResponse<SeoSettingResponse> update(@Valid @RequestBody SeoSettingRequest request) {
        SiteSetting setting = SiteSetting.builder()
                .blogTitle(request.blogTitle())
                .blogSubtitle(request.blogSubtitle())
                .blogDescription(request.blogDescription())
                .profileImageUrl(request.profileImageUrl())
                .metaDescription(request.metaDescription())
                .keywords(request.keywords())
                .robotsTxt(request.robotsTxt())
                .gaId(request.gaId())
                .gscVerification(request.gscVerification())
                .build();
        return ApiResponse.success(SeoSettingResponse.from(settingsService.update(setting)));
    }
}
