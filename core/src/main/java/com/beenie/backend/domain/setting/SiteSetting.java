package com.beenie.backend.domain.setting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteSetting {
    private Long id;
    private String blogTitle;
    private String blogSubtitle;
    private String blogDescription;
    private String profileImageUrl;
    private String metaDescription;
    private String keywords;
    private String robotsTxt;
    private String gaId;
    private String gscVerification;
    private LocalDateTime updatedAt;
}
