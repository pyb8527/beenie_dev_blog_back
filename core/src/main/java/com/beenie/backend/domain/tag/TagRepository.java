package com.beenie.backend.domain.tag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagRepository {

    List<Tag> findAll();

    Optional<Tag> findById(Long id);

    Optional<Tag> findByName(String name);

    List<Tag> findAllByNameIn(Collection<String> names);

    Tag save(Tag tag);

    void deleteById(Long id);

    List<TagCount> findAllWithUsageCount();

    /** 이름 목록 중 존재하지 않는 태그는 새로 생성하고, 전체 Tag 목록(id 포함)을 반환한다. */
    List<Tag> resolveOrCreate(Collection<String> names);
}
