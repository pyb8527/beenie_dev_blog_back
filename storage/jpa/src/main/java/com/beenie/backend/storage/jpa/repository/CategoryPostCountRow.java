package com.beenie.backend.storage.jpa.repository;

public record CategoryPostCountRow(Long id, String name, String slug, long postCount) {
}
