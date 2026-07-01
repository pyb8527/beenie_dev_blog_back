package com.beenie.backend.application.search;

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
public class SearchService {

    private static final int MIN_KEYWORD_LENGTH = 2;

    private final PostSearchRepository postSearchRepository;

    public PageResponse<PostSummary> search(String keyword, int page, int size) {
        if (keyword == null || keyword.trim().length() < MIN_KEYWORD_LENGTH) {
            throw new BusinessException(ErrorCode.SEARCH_KEYWORD_TOO_SHORT);
        }
        return postSearchRepository.searchPublic(keyword.trim(), page, size);
    }
}
