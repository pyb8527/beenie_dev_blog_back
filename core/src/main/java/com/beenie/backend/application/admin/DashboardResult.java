package com.beenie.backend.application.admin;

import com.beenie.backend.domain.comment.Comment;
import com.beenie.backend.domain.post.PostSummary;
import com.beenie.backend.domain.stats.VisitorTrendPoint;

import java.util.List;

public record DashboardResult(
        long visitorsToday,
        long visitorsThisWeek,
        long visitorsThisMonth,
        long totalPosts,
        long totalComments,
        List<PostSummary> topPosts,
        List<VisitorTrendPoint> visitorTrend,
        List<Comment> recentComments
) {
}
