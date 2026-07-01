package com.beenie.backend.support.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-500", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-400", "요청 값이 올바르지 않습니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON-404", "요청한 리소스를 찾을 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON-403", "접근 권한이 없습니다."),

    // Auth / User
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH-401", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401-1", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-401-2", "만료된 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AUTH-401-3", "리프레시 토큰을 찾을 수 없습니다."),
    REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "AUTH-401-4", "토큰 재사용이 감지되어 모든 세션이 무효화되었습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-404", "사용자를 찾을 수 없습니다."),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-404", "게시글을 찾을 수 없습니다."),
    POST_FORBIDDEN(HttpStatus.FORBIDDEN, "POST-403", "게시글에 대한 접근 권한이 없습니다."),
    POST_SLUG_DUPLICATE(HttpStatus.CONFLICT, "POST-409", "이미 존재하는 slug 입니다."),

    // Comment
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT-404", "댓글을 찾을 수 없습니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMENT-403", "댓글에 대한 권한이 없습니다."),
    COMMENT_PARENT_DELETED(HttpStatus.BAD_REQUEST, "COMMENT-400", "삭제된 댓글에는 답글을 작성할 수 없습니다."),
    COMMENT_PARENT_NOT_TOP_LEVEL(HttpStatus.BAD_REQUEST, "COMMENT-400-1", "대댓글에는 답글을 작성할 수 없습니다."),

    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY-404", "카테고리를 찾을 수 없습니다."),
    CATEGORY_DUPLICATE(HttpStatus.CONFLICT, "CATEGORY-409", "이미 존재하는 카테고리입니다."),
    CATEGORY_DELETE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "CATEGORY-400", "기본 카테고리는 삭제할 수 없습니다."),

    // Tag
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "TAG-404", "태그를 찾을 수 없습니다."),
    TAG_DUPLICATE(HttpStatus.CONFLICT, "TAG-409", "이미 존재하는 태그입니다."),

    // Search
    SEARCH_KEYWORD_TOO_SHORT(HttpStatus.BAD_REQUEST, "SEARCH-400", "검색어는 2자 이상이어야 합니다."),

    // File
    FILE_EMPTY(HttpStatus.BAD_REQUEST, "FILE-400", "업로드할 파일이 비어있습니다."),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "FILE-400-1", "파일 크기는 10MB를 초과할 수 없습니다."),
    FILE_INVALID_TYPE(HttpStatus.BAD_REQUEST, "FILE-400-2", "허용되지 않는 파일 형식입니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "FILE-500", "파일 업로드에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
