package com.beenie.backend.application.activity;

import com.beenie.backend.domain.activity.PostBookmarkRepository;
import com.beenie.backend.domain.activity.PostLikeRepository;
import com.beenie.backend.domain.post.Post;
import com.beenie.backend.domain.post.PostRepository;
import com.beenie.backend.domain.post.PostSearchRepository;
import com.beenie.backend.domain.post.PostStatus;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostSearchRepository postSearchRepository;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private PostBookmarkRepository postBookmarkRepository;

    private ActivityService activityService;

    @BeforeEach
    void setUp() {
        activityService = new ActivityService(postRepository, postSearchRepository, postLikeRepository, postBookmarkRepository);
    }

    @Test
    void togglingLikeOnAndOff() {
        Post post = Post.builder().id(1L).status(PostStatus.PUBLIC).likeCount(3).build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postLikeRepository.exists(10L, 1L)).thenReturn(false);

        var result = activityService.toggleLike(10L, 1L);

        assertThat(result.active()).isTrue();
        assertThat(result.count()).isEqualTo(4);
        verify(postLikeRepository).add(10L, 1L);
        verify(postRepository).changeLikeCount(1L, 1);
    }

    @Test
    void unlikingWhenAlreadyLiked() {
        Post post = Post.builder().id(1L).status(PostStatus.PUBLIC).likeCount(3).build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postLikeRepository.exists(10L, 1L)).thenReturn(true);

        var result = activityService.toggleLike(10L, 1L);

        assertThat(result.active()).isFalse();
        assertThat(result.count()).isEqualTo(2);
        verify(postLikeRepository).remove(10L, 1L);
        verify(postRepository).changeLikeCount(1L, -1);
    }

    @Test
    void throwsWhenTogglingLikeOnDeletedPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activityService.toggleLike(10L, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }

    @Test
    void togglingBookmarkOn() {
        Post post = Post.builder().id(2L).status(PostStatus.PUBLIC).bookmarkCount(0).build();
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(postBookmarkRepository.exists(5L, 2L)).thenReturn(false);

        var result = activityService.toggleBookmark(5L, 2L);

        assertThat(result.active()).isTrue();
        assertThat(result.count()).isEqualTo(1);
    }
}
