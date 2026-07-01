package com.beenie.backend.application.search;

import com.beenie.backend.domain.post.PostSearchRepository;
import com.beenie.backend.support.common.response.PageResponse;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private PostSearchRepository postSearchRepository;

    @Test
    void rejectsKeywordShorterThanTwoCharacters() {
        SearchService searchService = new SearchService(postSearchRepository);

        assertThatThrownBy(() -> searchService.search("a", 0, 10))
                .isInstanceOf(BusinessException.class)
                .extracting(e -> ((BusinessException) e).getErrorCode())
                .isEqualTo(ErrorCode.SEARCH_KEYWORD_TOO_SHORT);
    }

    @Test
    void rejectsBlankKeyword() {
        SearchService searchService = new SearchService(postSearchRepository);

        assertThatThrownBy(() -> searchService.search("  ", 0, 10))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void delegatesToRepositoryWhenKeywordIsValid() {
        SearchService searchService = new SearchService(postSearchRepository);
        when(postSearchRepository.searchPublic("spring", 0, 10)).thenReturn(PageResponse.of(List.of(), 0, 10, 0));

        var result = searchService.search("spring", 0, 10);

        assertThat(result.totalElements()).isZero();
    }
}
