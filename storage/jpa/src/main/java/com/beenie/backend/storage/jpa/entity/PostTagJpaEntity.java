package com.beenie.backend.storage.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "post_tags")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostTagJpaEntity {

    @EmbeddedId
    private PostTagId id;

    public PostTagJpaEntity(Long postId, Long tagId) {
        this.id = new PostTagId(postId, tagId);
    }

    @Embeddable
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PostTagId implements Serializable {
        @Column(name = "post_id")
        private Long postId;

        @Column(name = "tag_id")
        private Long tagId;
    }
}
