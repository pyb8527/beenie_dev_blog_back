package com.beenie.backend.storage.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "site_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SiteSettingJpaEntity {

    @Id
    private Long id;

    @Column(name = "blog_title", nullable = false, length = 200)
    private String blogTitle;

    @Column(name = "blog_subtitle", length = 200)
    private String blogSubtitle;

    @Column(name = "blog_description", length = 500)
    private String blogDescription;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "meta_description", length = 300)
    private String metaDescription;

    @Column(length = 500)
    private String keywords;

    @Lob
    @Column(name = "robots_txt", columnDefinition = "TEXT")
    private String robotsTxt;

    @Column(name = "ga_id", length = 50)
    private String gaId;

    @Column(name = "gsc_verification", length = 200)
    private String gscVerification;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
