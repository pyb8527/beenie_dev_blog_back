package com.beenie.backend.web;

import com.beenie.backend.support.common.response.ApiResponse;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesBusinessException() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleBusinessException(new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        assertThat(response.getStatusCode()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getStatus());
        assertThat(response.getBody().success()).isFalse();
        assertThat(response.getBody().error().code()).isEqualTo(ErrorCode.ENTITY_NOT_FOUND.getCode());
    }

    @Test
    void handlesUnexpectedException() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleException(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
        assertThat(response.getBody().success()).isFalse();
    }
}
