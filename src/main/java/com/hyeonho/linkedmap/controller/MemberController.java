package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/register")
    public Member register(@RequestBody RegisterRequest request) {
        System.out.println(request.getEmail());
        System.out.println(request.getUsername());
        System.out.println(request.getPassword());
        Member member = memberService.register(request);
        return member;
    }
}
