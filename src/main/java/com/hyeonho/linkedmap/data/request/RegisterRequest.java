package com.hyeonho.linkedmap.data.request;

import lombok.Builder;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Builder 사용시 주의점 (그냥 삽질하다가 알게된거임)
 * RequestBody, 이런 api요청으로 들어온 애들은 Jackson 을 사용해서 기본 생성자를 통해 JSON을 파싱하여 오브젝트로 만듬.
 * 그런데, Builder 를 사용하면 Jackson의 기본 생성자를 사용할수가 없음.
 */
@Getter
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private String profileImage;

//    @Builder
//    public RegisterRequest(String email, String password, String username, String profileImage) {
//        this.email = email;
//        this.password = password;
//        this.username = username;
//        this.profileImage = profileImage;
//    }
//    public RegisterRequest(String email, String password, String username) {
//        this.email = email;
//        this.password = password;
//        this.username = username;
//    }
}
