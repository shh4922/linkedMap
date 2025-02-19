package com.hyeonho.linkedmap;

import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.Role;
import com.hyeonho.linkedmap.repository.CategoryRepository;
import com.hyeonho.linkedmap.repository.CategoryUserRepository;
import com.hyeonho.linkedmap.repository.MarkerRepository;
import com.hyeonho.linkedmap.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MemberTest {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberTest(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Test
    public void createMember() {
        Member member = new Member("test222@test.com", "1111", "testUser2", Role.ROLE_USER);
        memberRepository.save(member);
    }

}