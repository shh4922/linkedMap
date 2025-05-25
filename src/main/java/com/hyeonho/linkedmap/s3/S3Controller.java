package com.hyeonho.linkedmap.s3;


import com.hyeonho.linkedmap.data.DefaultResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public static class SignRequest {
        public String folder;
        public String filename;
        public String contentType;
    }


    @PostMapping("/presigned")
    public ResponseEntity<DefaultResponse<Map<String, String>>> presigned(@RequestBody SignRequest request) {
        log.info("contentType -presigned {}", request.contentType);
        String key = request.folder + "/" + request.filename;
        String uploadUrl = s3Service.generateUploadUrl(key, request.contentType);

        String fileUrl = String.format(
                "https://%s.s3.ap-northeast-2.amazonaws.com/%s",
                s3Service.getBucket(), key
        );

        // https://linkedmap.s3.ap-northeast-2.amazonaws.com/markers/파일명
        // 파일명은 roomId
        Map<String, String> response = new HashMap<>();
        response.put("uploadUrl", uploadUrl);
        response.put("fileUrl", fileUrl);
        return ResponseEntity.ok(DefaultResponse.success(response));
    }
}
