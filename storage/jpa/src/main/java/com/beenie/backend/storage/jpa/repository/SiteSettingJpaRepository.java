package com.beenie.backend.storage.jpa.repository;

import com.beenie.backend.storage.jpa.entity.SiteSettingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSettingJpaRepository extends JpaRepository<SiteSettingJpaEntity, Long> {
}
