package com.beenie.backend.infrastructure.file;

import com.beenie.backend.domain.file.FileStorageRepository;
import com.beenie.backend.support.exception.BusinessException;
import com.beenie.backend.support.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorageAdapter implements FileStorageRepository {

    private final S3Client s3Client;

    @Value("${file.s3.bucket-name}")
    private String bucketName;

    @Value("${file.s3.endpoint}")
    private String endpoint;

    @PostConstruct
    void ensureBucketExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            try {
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
            } catch (Exception ex) {
                log.warn("S3 버킷 생성 실패 (bucket={}): {}", bucketName, ex.getMessage());
            }
        } catch (Exception e) {
            log.warn("S3 연결 확인 실패 (bucket={}): {}. 파일 업로드 기능이 정상 동작하지 않을 수 있습니다.", bucketName, e.getMessage());
        }
        applyPublicReadPolicy();
    }

    /**
     * 버킷은 기본적으로 비공개라 업로드는 성공해도 반환된 공개 URL(GET)이 403이 난다.
     * 블로그 썸네일/본문 이미지는 익명 조회가 가능해야 하므로 GetObject만 공개로 허용한다.
     */
    private void applyPublicReadPolicy() {
        String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucketName);
        try {
            s3Client.putBucketPolicy(PutBucketPolicyRequest.builder().bucket(bucketName).policy(policy).build());
        } catch (Exception e) {
            log.warn("S3 버킷 공개 읽기 정책 적용 실패 (bucket={}): {}", bucketName, e.getMessage());
        }
    }

    @Override
    public String upload(String key, byte[] content, String contentType) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucketName).key(key).contentType(contentType).build(),
                    RequestBody.fromBytes(content));
        } catch (Exception e) {
            log.error("S3 업로드 실패 key={}", key, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }
        return endpoint + "/" + bucketName + "/" + key;
    }
}
