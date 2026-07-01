package com.beenie.backend.application.tag;

import com.beenie.backend.domain.tag.Tag;
import com.beenie.backend.domain.tag.TagCount;
import com.beenie.backend.domain.tag.TagRepository;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    public List<TagCount> listAllWithUsageCount() {
        return tagRepository.findAllWithUsageCount();
    }

    @Transactional
    public Tag update(Long id, String name) {
        Tag tag = tagRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TAG_NOT_FOUND));
        tagRepository.findByName(name).filter(existing -> !existing.getId().equals(id)).ifPresent(existing -> {
            throw new BusinessException(ErrorCode.TAG_DUPLICATE);
        });
        tag.setName(name);
        return tagRepository.save(tag);
    }

    @Transactional
    public void delete(Long id) {
        tagRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.TAG_NOT_FOUND));
        tagRepository.deleteById(id);
    }
}
