package com.hyeonho.linkedmap.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @GetMapping("/kakao/auth")
    public Map<String, String > getKakaoAuth(@RequestParam("code") String code) {
        log.info("code :{}",code);
        Map<String,String> req = new HashMap<>();
        return req;
    }

//    @GetMapping("/auth/refresh")
//    public Map<String, String > getAuthRefresh(@RequestParam("refresh_token") String refreshToken) {
//
//    }

}
