package com.beenie.backend.application.post;

import com.beenie.backend.domain.activity.PostBookmarkRepository;
import com.beenie.backend.domain.activity.PostLikeRepository;
import com.beenie.backend.domain.category.Category;
import com.beenie.backend.domain.category.CategoryRepository;
import com.beenie.backend.domain.comment.CommentRepository;
import com.beenie.backend.domain.event.PostEventPublisher;
import com.beenie.backend.domain.post.Post;
import com.beenie.backend.domain.post.PostRepository;
import com.beenie.backend.domain.post.PostSearchRepository;
import com.beenie.backend.domain.post.PostStatus;
import com.beenie.backend.domain.post.ViewCountDedupRepository;
import com.beenie.backend.domain.tag.Tag;
import com.beenie.backend.domain.tag.TagRepository;
import com.beenie.backend.infrastructure.markdown.MarkdownProcessor;
import com.beenie.backend.storage.redis.PostListCacheStore;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostSearchRepository postSearchRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostEventPublisher postEventPublisher;
    @Mock
    private ViewCountDedupRepository viewCountDedupRepository;
    @Mock
    private PostListCacheStore postListCacheStore;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private PostBookmarkRepository postBookmarkRepository;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(
                postRepository, postSearchRepository, categoryRepository, tagRepository, commentRepository,
                new MarkdownProcessor(), postEventPublisher, viewCountDedupRepository, postListCacheStore,
                new ObjectMapper(), postLikeRepository, postBookmarkRepository);
    }

    @Test
    void createsPostWithGeneratedSlugAndDefaultCategoryWhenNotProvided() {
        when(postRepository.existsBySlug(anyString())).thenReturn(false);
        when(tagRepository.resolveOrCreate(anyList())).thenReturn(List.of(Tag.builder().id(1L).name("spring").build()));
        when(postRepository.create(any(Post.class), anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PostCreateCommand request = new PostCreateCommand(
                "Hello Spring Boot", "본문 내용입니다.", null, List.of("spring"), "PUBLIC", null, null);

        Post created = postService.create(request, 1L);

        assertThat(created.getSlug()).isEqualTo("hello-spring-boot");
        assertThat(created.getCategoryId()).isEqualTo(Category.UNCATEGORIZED_ID);
        assertThat(created.getStatus()).isEqualTo(PostStatus.PUBLIC);
        verify(postListCacheStore).evictAll();
        verify(postEventPublisher).publishPostChanged();
    }

    @Test
    void appendsNumericSuffixWhenSlugAlreadyExists() {
        when(postRepository.existsBySlug("my-title")).thenReturn(true);
        when(postRepository.existsBySlug("my-title-1")).thenReturn(false);
        when(tagRepository.resolveOrCreate(anyList())).thenReturn(List.of());
        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.create(captor.capture(), anyList())).thenAnswer(inv -> captor.getValue());

        PostCreateCommand request = new PostCreateCommand(
                "My Title", "content", null, List.of(), "DRAFT", null, null);

        Post created = postService.create(request, 1L);

        assertThat(created.getSlug()).isEqualTo("my-title-1");
    }

    @Test
    void throwsWhenCategoryDoesNotExist() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        PostCreateCommand request = new PostCreateCommand(
                "Title", "content", 99L, List.of(), "DRAFT", null, null);

        assertThatThrownBy(() -> postService.create(request, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);

        verify(postRepository, never()).create(any(), anyList());
    }

    @Test
    void autoExtractsThumbnailFromFirstMarkdownImageWhenNotProvided() {
        when(postRepository.existsBySlug(anyString())).thenReturn(false);
        when(tagRepository.resolveOrCreate(anyList())).thenReturn(List.of());
        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.create(captor.capture(), anyList())).thenAnswer(inv -> captor.getValue());

        String content = "intro\n\n![alt](https://cdn.example.com/thumb.png)\n\nmore";
        PostCreateCommand request = new PostCreateCommand(
                "Title", content, null, List.of(), "PUBLIC", null, null);

        Post created = postService.create(request, 1L);

        assertThat(created.getThumbnailUrl()).isEqualTo("https://cdn.example.com/thumb.png");
    }

    @Test
    void deletePerformsSoftDeleteAndCascadesToComments() {
        when(postRepository.findById(5L)).thenReturn(Optional.of(Post.builder().id(5L).status(PostStatus.PUBLIC).build()));

        postService.delete(5L);

        verify(postRepository).softDelete(5L);
        verify(commentRepository).softDeleteAllByPostId(5L);
        verify(postListCacheStore).evictAll();
    }

    @Test
    void deleteThrowsWhenPostAlreadyDeleted() {
        when(postRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.delete(5L))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.POST_NOT_FOUND);
    }
}
