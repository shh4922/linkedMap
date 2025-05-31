package com.hyeonho.linkedmap.error;

import com.hyeonho.linkedmap.data.DefaultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;

/**
 * 여기에 전역에러 설정하면 저거 리턴하면 Controller에서 따로 try catch d안해도됨
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 500
     * 서버 에러*/
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<DefaultResponse<Map<String,String>>> handleDatabaseException(DatabaseException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DefaultResponse.error(500, e.getMessage()));
    }

    /** 400 에러.
     *  요청 잘못 보낸경우 */
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<DefaultResponse<Map<String,String>>> handleInvalidException(InvalidRequestException e) {
        return ResponseEntity.badRequest()
                .body(DefaultResponse.error(400,e.getMessage()));
    }

    /** 409 에러
     * 중복된 계정 */
    @ExceptionHandler(DuplicateMemberException.class)
    public ResponseEntity<DefaultResponse<Map<String,String>>> handleDuplicateMemberException(DuplicateMemberException e) {
        return ResponseEntity.status(409)
                .body(DefaultResponse.error(409,e.getMessage()));
    }

    /**
     * 403 에러
     * 권한 에러
     */
    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<DefaultResponse<Map<String,String>>> handlePermissionException(PermissionException e) {
        return ResponseEntity.status(403)
                .body(DefaultResponse.error(403,e.getMessage()));
    }


    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<DefaultResponse<String>> handleMissingPart(MissingServletRequestPartException ex) {
        String partName = ex.getRequestPartName();
        return ResponseEntity.badRequest()
                .body(DefaultResponse.error(400, "요청 파라미터 [" + partName + "]가 누락되었습니다."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultResponse<String>> handleValidationError(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest()
                .body(DefaultResponse.error(400, "유효하지 않은 요청입니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultResponse<String>> handleGeneral(Exception ex) {
        ex.printStackTrace(); // 로그 확인용
        return ResponseEntity.status(500)
                .body(DefaultResponse.error(500, "서버 내부 오류"));
    }
}
