package com.hyeonho.linkedmap.s3;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

@Service
@Getter

public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner presigner;
    private final String bucket = "linkedmap";

    public S3Service(S3Presigner presigner , S3Client s3Client) {
        this.presigner = presigner;
        this.s3Client = s3Client;
    }

    /**
     * 주어진 키와 콘텐츠 타입으로 PUT 전용 프리사인드 URL을 발급합니다.
     * @param key S3 객체 키(예: markers/123/icon.png)
     * @param contentType 파일의 MIME 타입
     * @return presigned URL 문자열
     */
    public String generateUploadUrl(String key, String contentType) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        // 5분 동안 유효한 URL 생성
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    public void moveImage(String fromKey, String toKey) {
        copyImage(fromKey, toKey);
        deleteImage(fromKey);
    }

    public void copyImage(String fromKey, String toKey) {
        CopyObjectRequest copyRequest = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(fromKey)
                .destinationBucket(bucket)
                .destinationKey(toKey)
                .acl(ObjectCannedACL.BUCKET_OWNER_FULL_CONTROL)
                .build();
        s3Client.copyObject(copyRequest);
    }

    public void deleteImage(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    public String getKeyFromUrl(String fullUrl) {
        String bucketUrlPrefix = "https://linkedmap.s3.ap-northeast-2.amazonaws.com/";
        if (!fullUrl.startsWith(bucketUrlPrefix)) {
            throw new IllegalArgumentException("잘못된 S3 URL입니다.");
        }
        return fullUrl.substring(bucketUrlPrefix.length());
    }

    public String upload(MultipartFile file, String key) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + key;

        } catch (IOException e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }
}
