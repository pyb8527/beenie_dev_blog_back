package com.beenie.backend.infrastructure.file;

public record ProcessedImage(byte[] content, String contentType, String extension) {
}
