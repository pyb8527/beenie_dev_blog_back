package com.beenie.backend.application.admin;

import com.beenie.backend.domain.setting.SiteSetting;
import com.beenie.backend.domain.setting.SiteSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettingsService {

    private final SiteSettingRepository siteSettingRepository;

    public SiteSetting get() {
        return siteSettingRepository.get();
    }

    @Transactional
    public SiteSetting update(SiteSetting setting) {
        return siteSettingRepository.save(setting);
    }
}
