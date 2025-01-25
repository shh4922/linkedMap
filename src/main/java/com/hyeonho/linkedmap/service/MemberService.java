package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member register(RegisterRequest member) {
        Member member1 = new Member(member.getEmail(), member.getPassword(), member.getUsername());
        return memberRepository.save(member1);
    }

    public Member findByEmail(String email) {
        return memberRepository.findById(email).orElseThrow();
    }
}
