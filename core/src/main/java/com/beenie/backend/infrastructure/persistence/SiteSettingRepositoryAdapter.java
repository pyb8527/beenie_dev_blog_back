package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.setting.SiteSetting;
import com.beenie.backend.domain.setting.SiteSettingRepository;
import com.beenie.backend.storage.jpa.entity.SiteSettingJpaEntity;
import com.beenie.backend.storage.jpa.repository.SiteSettingJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SiteSettingRepositoryAdapter implements SiteSettingRepository {

    private static final Long SINGLETON_ID = 1L;

    private final SiteSettingJpaRepository siteSettingJpaRepository;

    @Override
    public SiteSetting get() {
        return siteSettingJpaRepository.findById(SINGLETON_ID)
                .map(SiteSettingRepositoryAdapter::toDomain)
                .orElseGet(() -> SiteSetting.builder().id(SINGLETON_ID).blogTitle("Beenie Dev Blog").build());
    }

    @Override
    public SiteSetting save(SiteSetting setting) {
        SiteSettingJpaEntity entity = SiteSettingJpaEntity.builder()
                .id(SINGLETON_ID)
                .blogTitle(setting.getBlogTitle())
                .blogSubtitle(setting.getBlogSubtitle())
                .blogDescription(setting.getBlogDescription())
                .profileImageUrl(setting.getProfileImageUrl())
                .metaDescription(setting.getMetaDescription())
                .keywords(setting.getKeywords())
                .robotsTxt(setting.getRobotsTxt())
                .gaId(setting.getGaId())
                .gscVerification(setting.getGscVerification())
                .build();
        return toDomain(siteSettingJpaRepository.save(entity));
    }

    private static SiteSetting toDomain(SiteSettingJpaEntity entity) {
        return SiteSetting.builder()
                .id(entity.getId())
                .blogTitle(entity.getBlogTitle())
                .blogSubtitle(entity.getBlogSubtitle())
                .blogDescription(entity.getBlogDescription())
                .profileImageUrl(entity.getProfileImageUrl())
                .metaDescription(entity.getMetaDescription())
                .keywords(entity.getKeywords())
                .robotsTxt(entity.getRobotsTxt())
                .gaId(entity.getGaId())
                .gscVerification(entity.getGscVerification())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
