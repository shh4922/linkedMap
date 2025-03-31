package com.hyeonho.linkedmap;


import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.Role;
import com.hyeonho.linkedmap.repository.MemberRepository;
import com.hyeonho.linkedmap.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MemberTest {

    private static final Logger log = LoggerFactory.getLogger(MemberTest.class);
    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Test
    void createMember() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test111")
                .password("test111")
                .username("test111").build();

        Member member = Member.builder()
                .email("test111")
                .password("encodePassword")
                .username("test111")
                .role(Role.ROLE_USER)
                .build();

        when(memberRepository.findById(request.getEmail())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(request.getPassword())).thenReturn("encodePassword");
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member memberArg = invocation.getArgument(0);


            return new Member(memberArg.getEmail(), memberArg.getPassword(), memberArg.getUsername(), memberArg.getRole());
        });


        try {
            Member member1 = memberService.register(request);
            log.info("findByEmail 호출됨: {}", member1.getEmail());
            log.info("password 호출됨: {}", member1.getPassword());
            log.info("name 호출됨: {}", member1.getUsername());
//            Optional<Member> member = findByEmail(request.getEmail());
//            log.info("조회 결과: {}", member.isPresent());

        } catch (Exception e) {
            e.printStackTrace();
        }

//        verify(memberRepository, times(1)).findById(request.getEmail());
        verify(memberRepository, times(1)).save(any(Member.class));
        verify(bCryptPasswordEncoder, times(1)).encode(request.getPassword());
    }



}