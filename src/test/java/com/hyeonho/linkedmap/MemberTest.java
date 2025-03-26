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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MemberTest {

//    private final MemberRepository memberRepository;
//
//    @Autowired
//    public MemberTest(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void createMember() {
        Member member = new Member("test111@test.com", "1111", "testUser1", Role.ROLE_USER);

        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member savedMember = memberRepository.save(member);
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void 비밀번호_암호화_후_유저생성() {
        String rawPassword = "password1";
        String encodePassword = "asdqweasfsadf@asdqwe";

        RegisterRequest request = new RegisterRequest();
        request.setEmail("test111");
        request.setUsername("testUser");
        request.setPassword(rawPassword);

        Member member = new Member(request.getEmail(), encodePassword, request.getUsername(), Role.ROLE_USER);

        when(bCryptPasswordEncoder.encode(rawPassword)).thenReturn(encodePassword);
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member savedMember = memberService.register(request);

//        assertEquals(encodePassword, savedMember.getPassword());
//        verify(bCryptPasswordEncoder, times(1)).encode(rawPassword);
//        verify(memberRepository, times(1)).save(member);
    }

}