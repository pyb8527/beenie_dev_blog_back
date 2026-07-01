package com.beenie.backend.domain.activity;

public interface PostBookmarkRepository {

    boolean exists(Long userId, Long postId);

    void add(Long userId, Long postId);

    void remove(Long userId, Long postId);
}
