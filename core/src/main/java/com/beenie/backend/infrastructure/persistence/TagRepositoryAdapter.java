package com.beenie.backend.infrastructure.persistence;

import com.beenie.backend.domain.tag.Tag;
import com.beenie.backend.domain.tag.TagCount;
import com.beenie.backend.domain.tag.TagRepository;
import com.beenie.backend.storage.jpa.entity.TagJpaEntity;
import com.beenie.backend.storage.jpa.repository.TagJpaRepository;
import com.beenie.backend.storage.jpa.repository.TagUsageCountRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TagRepositoryAdapter implements TagRepository {

    private final TagJpaRepository tagJpaRepository;

    @Override
    public List<Tag> findAll() {
        return tagJpaRepository.findAll().stream().map(TagRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Optional<Tag> findById(Long id) {
        return tagJpaRepository.findById(id).map(TagRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        return tagJpaRepository.findByName(name).map(TagRepositoryAdapter::toDomain);
    }

    @Override
    public List<Tag> findAllByNameIn(Collection<String> names) {
        return tagJpaRepository.findAllByNameIn(names).stream().map(TagRepositoryAdapter::toDomain).toList();
    }

    @Override
    public Tag save(Tag tag) {
        TagJpaEntity entity = TagJpaEntity.builder().id(tag.getId()).name(tag.getName()).build();
        return toDomain(tagJpaRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        tagJpaRepository.deleteById(id);
    }

    @Override
    public List<TagCount> findAllWithUsageCount() {
        return tagJpaRepository.findAllWithUsageCount().stream()
                .map(row -> new TagCount(row.id(), row.name(), row.useCount()))
                .toList();
    }

    @Override
    public List<Tag> resolveOrCreate(Collection<String> names) {
        if (names == null || names.isEmpty()) {
            return List.of();
        }
        List<String> normalized = names.stream().map(String::trim).filter(n -> !n.isEmpty()).distinct().toList();
        List<TagJpaEntity> existing = tagJpaRepository.findAllByNameIn(normalized);
        List<String> existingNames = existing.stream().map(TagJpaEntity::getName).toList();
        List<TagJpaEntity> created = normalized.stream()
                .filter(n -> !existingNames.contains(n))
                .map(n -> tagJpaRepository.save(TagJpaEntity.builder().name(n).build()))
                .toList();
        return java.util.stream.Stream.concat(existing.stream(), created.stream())
                .map(TagRepositoryAdapter::toDomain)
                .toList();
    }

    private static Tag toDomain(TagJpaEntity entity) {
        return Tag.builder().id(entity.getId()).name(entity.getName()).createdAt(entity.getCreatedAt()).build();
    }
}
