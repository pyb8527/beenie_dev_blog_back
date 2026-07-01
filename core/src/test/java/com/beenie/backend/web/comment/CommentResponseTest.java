package com.beenie.backend.web.comment;

import com.beenie.backend.domain.comment.Comment;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CommentResponseTest {

    @Test
    void buildsNestedTreeFromFlatList() {
        Comment top = Comment.builder().id(1L).parentId(null).content("top").authorName("beenie")
                .createdAt(LocalDateTime.now()).build();
        Comment reply = Comment.builder().id(2L).parentId(1L).content("reply").authorName("guest")
                .createdAt(LocalDateTime.now()).build();

        List<CommentResponse> tree = CommentResponse.buildTree(List.of(top, reply));

        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).replies()).hasSize(1);
        assertThat(tree.get(0).replies().get(0).content()).isEqualTo("reply");
    }

    @Test
    void hidesDeletedTopLevelCommentWithoutReplies() {
        Comment deleted = Comment.builder().id(1L).parentId(null).deleted(true).content("gone")
                .createdAt(LocalDateTime.now()).build();

        List<CommentResponse> tree = CommentResponse.buildTree(List.of(deleted));

        assertThat(tree).isEmpty();
    }

    @Test
    void showsPlaceholderForDeletedTopLevelCommentThatStillHasReplies() {
        Comment deletedTop = Comment.builder().id(1L).parentId(null).deleted(true).content("gone")
                .createdAt(LocalDateTime.now()).build();
        Comment reply = Comment.builder().id(2L).parentId(1L).content("still here").authorName("guest")
                .createdAt(LocalDateTime.now()).build();

        List<CommentResponse> tree = CommentResponse.buildTree(List.of(deletedTop, reply));

        assertThat(tree).hasSize(1);
        assertThat(tree.get(0).isDeleted()).isTrue();
        assertThat(tree.get(0).content()).isEqualTo("이 댓글은 삭제되었습니다.");
        assertThat(tree.get(0).replies()).hasSize(1);
    }
}
