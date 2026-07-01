package com.beenie.backend.domain.file;

public interface FileStorageRepository {

    /**
     * @param key         저장 경로 (예: posts/2026/07/uuid.webp)
     * @param content     파일 바이트
     * @param contentType MIME 타입
     * @return 공개 접근 가능한 URL
     */
    String upload(String key, byte[] content, String contentType);
}
