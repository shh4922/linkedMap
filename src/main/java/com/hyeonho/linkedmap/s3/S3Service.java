package com.hyeonho.linkedmap.s3;

import lombok.Getter;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Service
@Getter
public class S3Service {

    private final S3Presigner presigner;
    private final String bucket = "linkedmap";

    public S3Service(S3Presigner presigner) {
        this.presigner = presigner;
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

//    public void copyRequestToS3(String key, String contentType) {
//        CopyObjectRequest copyReq = CopyObjectRequest.builder()
//                .sourceBucket(bucket)
//                .sourceKey("rooms/temp-91f3a123/IMG_123.jpg")
//                .destinationBucket("linkedmap")
//                .destinationKey("rooms/42/IMG_123.jpg")
//                .build();
//        s3Client.copyObject(copyReq);
//    }


}
