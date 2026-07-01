package com.beenie.backend.application.activity;

import com.beenie.backend.domain.activity.PostBookmarkRepository;
import com.beenie.backend.domain.activity.PostLikeRepository;
import com.beenie.backend.domain.post.Post;
import com.beenie.backend.domain.post.PostRepository;
import com.beenie.backend.domain.post.PostSearchRepository;
import com.beenie.backend.domain.post.PostSummary;
import com.beenie.backend.support.common.response.PageResponse;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ActivityService {

    private final PostRepository postRepository;
    private final PostSearchRepository postSearchRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostBookmarkRepository postBookmarkRepository;

    @Transactional
    public ToggleResult toggleLike(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        boolean alreadyLiked = postLikeRepository.exists(userId, postId);
        if (alreadyLiked) {
            postLikeRepository.remove(userId, postId);
            postRepository.changeLikeCount(postId, -1);
            return new ToggleResult(false, Math.max(0, post.getLikeCount() - 1));
        } else {
            postLikeRepository.add(userId, postId);
            postRepository.changeLikeCount(postId, 1);
            return new ToggleResult(true, post.getLikeCount() + 1);
        }
    }

    @Transactional
    public ToggleResult toggleBookmark(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        boolean alreadyBookmarked = postBookmarkRepository.exists(userId, postId);
        if (alreadyBookmarked) {
            postBookmarkRepository.remove(userId, postId);
            postRepository.changeBookmarkCount(postId, -1);
            return new ToggleResult(false, Math.max(0, post.getBookmarkCount() - 1));
        } else {
            postBookmarkRepository.add(userId, postId);
            postRepository.changeBookmarkCount(postId, 1);
            return new ToggleResult(true, post.getBookmarkCount() + 1);
        }
    }

    public PageResponse<PostSummary> myBookmarks(Long userId, int page, int size) {
        return postSearchRepository.findBookmarkedByUser(userId, page, size);
    }

    public record ToggleResult(boolean active, long count) {
    }
}
