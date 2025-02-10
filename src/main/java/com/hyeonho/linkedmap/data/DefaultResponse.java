package com.hyeonho.linkedmap.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultResponse<T> {
    private int status;     // HTTP 상태 코드 (200, 400, 500 등)
    private String message; // 응답 메시지 ("성공", "에러 발생" 등)
    private T data;         // 실제 데이터 (DTO나 엔티티)

    // 성공 응답 (데이터 포함)
    public static <T> DefaultResponse<T> success(T data) {
        return new DefaultResponse<>(200, "성공", data);
    }

    // 실패 응답
    public static <T> DefaultResponse<T> error(int status, String message) {
        return new DefaultResponse<>(status, message, null);
    }
}