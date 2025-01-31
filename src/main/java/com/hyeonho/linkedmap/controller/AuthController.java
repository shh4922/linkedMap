package com.hyeonho.linkedmap.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
//    https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=99cb94f9615fab01a4413fd64a901e34&redirect_uri=http://localhost:8080/api/v1/kakao/auth

    @GetMapping("/kakao/auth")
    public Map<String, String > getKakaoAuth(@RequestParam("code") String code) {
        System.out.println(code);
        Map<String,String> req = new HashMap<>();
        return req;
    }


}
