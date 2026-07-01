package com.beenie.backend.application.admin;

import com.beenie.backend.domain.comment.CommentRepository;
import com.beenie.backend.domain.post.AdminPostListQuery;
import com.beenie.backend.domain.post.PostListQuery;
import com.beenie.backend.domain.post.PostSearchRepository;
import com.beenie.backend.domain.post.PostSort;
import com.beenie.backend.domain.stats.VisitorStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final VisitorStatsRepository visitorStatsRepository;
    private final PostSearchRepository postSearchRepository;
    private final CommentRepository commentRepository;

    public DashboardResult getDashboard() {
        long totalPosts = postSearchRepository
                .findAdminList(AdminPostListQuery.builder().page(0).size(1).build())
                .totalElements();

        var topPosts = postSearchRepository
                .findPublicList(PostListQuery.builder().sort(PostSort.POPULAR).page(0).size(5).build())
                .content();

        var recentComments = commentRepository.findAllForAdmin(null, 0, 5);

        return new DashboardResult(
                visitorStatsRepository.countToday(),
                visitorStatsRepository.countThisWeek(),
                visitorStatsRepository.countThisMonth(),
                totalPosts,
                commentRepository.countAll(),
                topPosts,
                visitorStatsRepository.recentTrend(30),
                recentComments);
    }
}
