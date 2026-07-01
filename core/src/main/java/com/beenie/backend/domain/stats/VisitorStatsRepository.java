package com.beenie.backend.domain.stats;

import java.util.List;

public interface VisitorStatsRepository {

    void recordVisit(String clientKey);

    long countToday();

    long countThisWeek();

    long countThisMonth();

    /** 오늘로부터 최근 {@code days}일간의 일별 방문자 수 추이 (오래된 날짜 → 오늘 순). */
    List<VisitorTrendPoint> recentTrend(int days);
}
