package com.beenie.backend.domain.setting;

public interface SiteSettingRepository {

    SiteSetting get();

    SiteSetting save(SiteSetting setting);
}
