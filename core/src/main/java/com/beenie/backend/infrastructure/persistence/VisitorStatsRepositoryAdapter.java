package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.stats.VisitorStatsRepository;
import com.beenie.backend.domain.stats.VisitorTrendPoint;
import com.beenie.backend.storage.redis.VisitorStatsRedisStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VisitorStatsRepositoryAdapter implements VisitorStatsRepository {

    private final VisitorStatsRedisStore store;

    @Override
    public void recordVisit(String clientKey) {
        store.recordVisit(clientKey, LocalDate.now());
    }

    @Override
    public long countToday() {
        return store.countVisitors(LocalDate.now());
    }

    @Override
    public long countThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        return store.countVisitors(monday, today);
    }

    @Override
    public long countThisMonth() {
        LocalDate today = LocalDate.now();
        LocalDate first = today.withDayOfMonth(1);
        return store.countVisitors(first, today);
    }

    @Override
    public List<VisitorTrendPoint> recentTrend(int days) {
        LocalDate today = LocalDate.now();
        List<VisitorTrendPoint> trend = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            trend.add(new VisitorTrendPoint(date, store.countVisitors(date)));
        }
        return trend;
    }
}
