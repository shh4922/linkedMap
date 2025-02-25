package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.data.dto.LoginResponseDTO;
import com.hyeonho.linkedmap.data.request.LoginRequestDTO;
import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /** 회원가입 */
    @PostMapping("/register")
    public Member register(@RequestBody RegisterRequest request) {
        Member member = memberService.register(request);
        return member;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        LoginResponseDTO responseDTO = memberService.login(request);
        return responseDTO;
    }

}
