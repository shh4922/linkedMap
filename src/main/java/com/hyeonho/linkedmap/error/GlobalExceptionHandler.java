package com.hyeonho.linkedmap.error;

import com.hyeonho.linkedmap.data.DefaultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 여기에 전역에러 설정하면 저거 리턴하면 Controller에서 따로 try catch d안해도됨
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<DefaultResponse<Map<String,String>>> handleDatabaseException(DatabaseException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DefaultResponse.error(500, e.getMessage()));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<DefaultResponse<Map<String,String>>> handleInvalidException(InvalidRequestException e) {
        return ResponseEntity.badRequest()
                .body(DefaultResponse.error(400,e.getMessage()));
    }

    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<DefaultResponse<Map<String,String>>> handleDuplicateMemberException(DuplicateMemberException e) {
        return ResponseEntity.status(409)
                .body(DefaultResponse.error(409,e.getMessage()));
    }
}
