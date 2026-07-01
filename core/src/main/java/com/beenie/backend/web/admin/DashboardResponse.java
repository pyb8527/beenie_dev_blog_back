package com.beenie.backend.web.admin;

import com.beenie.backend.application.admin.DashboardResult;
import com.beenie.backend.domain.comment.Comment;
import com.beenie.backend.domain.stats.VisitorTrendPoint;
import com.beenie.backend.web.post.PostSummaryResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DashboardResponse(
        long visitorsToday,
        long visitorsThisWeek,
        long visitorsThisMonth,
        long totalPosts,
        long totalComments,
        List<PostSummaryResponse> topPosts,
        List<TrendPoint> visitorTrend,
        List<RecentComment> recentComments
) {
    public static DashboardResponse from(DashboardResult result) {
        return new DashboardResponse(
                result.visitorsToday(), result.visitorsThisWeek(), result.visitorsThisMonth(),
                result.totalPosts(), result.totalComments(),
                result.topPosts().stream().map(PostSummaryResponse::from).toList(),
                result.visitorTrend().stream().map(TrendPoint::from).toList(),
                result.recentComments().stream().map(RecentComment::from).toList());
    }

    public record TrendPoint(LocalDate date, long count) {
        public static TrendPoint from(VisitorTrendPoint point) {
            return new TrendPoint(point.date(), point.count());
        }
    }

    public record RecentComment(
            Long id, Long postId, String authorName, String authorAvatarUrl, String content, LocalDateTime createdAt
    ) {
        public static RecentComment from(Comment comment) {
            return new RecentComment(comment.getId(), comment.getPostId(), comment.getAuthorName(),
                    comment.getAuthorAvatarUrl(), comment.getContent(), comment.getCreatedAt());
        }
    }
}
