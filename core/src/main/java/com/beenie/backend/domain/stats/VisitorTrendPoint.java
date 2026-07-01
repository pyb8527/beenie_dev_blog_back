package com.beenie.backend.domain.stats;

import java.time.LocalDate;

public record VisitorTrendPoint(LocalDate date, long count) {
}
