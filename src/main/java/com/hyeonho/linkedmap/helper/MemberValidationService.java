package com.hyeonho.linkedmap.helper;

import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.member.Member;
import com.hyeonho.linkedmap.member.MemberRepository;
import com.hyeonho.linkedmap.member.MemberService;
import com.hyeonho.linkedmap.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberValidationService {

    private final MemberRepository memberRepository;


    public Member findMemberById(Long id) {
        return memberRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new InvalidRequestException("해당 계정 없음"));
    }
}
