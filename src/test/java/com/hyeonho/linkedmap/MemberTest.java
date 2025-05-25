package com.hyeonho.linkedmap;


import com.hyeonho.linkedmap.member.MemberRepository;
import com.hyeonho.linkedmap.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

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
//        RegisterRequest request = RegisterRequest.builder()
//                .email("test999")
//                .password("test999")
//                .username("test999").build();

//        log.info("request {}", request.getEmail());
//        log.info("request {}", request.getUsername());
//        log.info("request {}", request.getUsername());

//        Member member = Member.builder()
//                .email("test999")
//                .password("encodePassword")
//                .username("test999")
//                .role(Role.ROLE_USER)
//                .build();
//        log.info("resultMember {}", member.getPassword());
//        log.info("resultMember {}", member.getUsername());
//        log.info("resultMember {}", member.getEmail());
//        log.info("resultMember {}", member.getRole());


//        when(memberRepository.findById(request.getEmail())).thenReturn(Optional.empty());
//        when(bCryptPasswordEncoder.encode(request.getPassword())).thenReturn("encodePassword");
//        when(memberRepository.save(any(Member.class))).thenReturn(member);


//        try {
//            Member member1 = memberService.register(request);
//            log.info("findByEmail 호출됨: {}", member1.getEmail());
//            log.info("password 호출됨: {}", member1.getPassword());
//            log.info("name 호출됨: {}", member1.getUsername());
//
//            // 추가된 부분: member1이 member와 동일한지 확인
//            assertEquals(member.getEmail(), member1.getEmail());
//            assertEquals(member.getPassword(), member1.getPassword());
//            assertEquals(member.getUsername(), member1.getUsername());
//            assertEquals(member.getRole(), member1.getRole());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        verify(memberRepository, times(1)).findById(request.getEmail());
//        verify(memberRepository, times(1)).save(any(Member.class));
//        verify(bCryptPasswordEncoder, times(1)).encode(request.getPassword());
    }



}