package com.beenie.backend.application.file;

import com.beenie.backend.domain.file.FileStorageRepository;
import com.beenie.backend.infrastructure.file.ImageProcessor;
import com.beenie.backend.infrastructure.file.ProcessedImage;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final long MAX_SIZE_BYTES = 10L * 1024 * 1024;
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");

    private final ImageProcessor imageProcessor;
    private final FileStorageRepository fileStorageRepository;

    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE);
        }
        String originalExt = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(originalExt)) {
            throw new BusinessException(ErrorCode.FILE_INVALID_TYPE);
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        ProcessedImage processed = imageProcessor.process(bytes);
        String key = buildKey(processed.extension());
        return fileStorageRepository.upload(key, processed.content(), processed.contentType());
    }

    private String buildKey(String extension) {
        LocalDate now = LocalDate.now();
        return "posts/%d/%02d/%s.%s".formatted(now.getYear(), now.getMonthValue(), UUID.randomUUID(), extension);
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
}
