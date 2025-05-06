package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.config.JWTProvider;
import com.hyeonho.linkedmap.config.RefreshToken;
import com.hyeonho.linkedmap.data.dto.member.LoginResponseDTO;
import com.hyeonho.linkedmap.data.dto.member.MemberInfoDTO;
import com.hyeonho.linkedmap.data.dto.member.MemberUpdateDto;
import com.hyeonho.linkedmap.data.dto.member.RegisterDTO;
import com.hyeonho.linkedmap.data.request.LoginRequestDTO;
import com.hyeonho.linkedmap.data.request.MemberUpdateRequest;
import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.entity.RoomMember;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.RoomMemberRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.enumlist.Role;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.DuplicateMemberException;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final RoomService roomService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JWTProvider jwtProvider;

    /** 회원가입 */
    public RegisterDTO register(RegisterRequest request) {
        Optional<Member> member = findByEmail(request.getEmail());
        if(member.isPresent()) { throw new DuplicateMemberException("중복된 계정임");}

        try {
            String password = bCryptPasswordEncoder.encode(request.getPassword());
            Member member1 = Member.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(password)
                    .profileImage(request.getProfileImage())
                    .role(Role.ROLE_USER)
                    .build();
            Member saveMember = memberRepository.save(member1);

            return RegisterDTO.builder()
                    .member(saveMember)
                    .build();
        } catch (DatabaseException e) {
            throw new DatabaseException("회원가입-디비오류");
        }
    }

    /** 내 정보 조회 */
    public MemberInfoDTO getMyInfoById(Long id) {
        Member member = findMemberById(id);

        return MemberInfoDTO.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .role(member.getRole().name())
                .profileImage(member.getProfileImage())
                .createdAt(member.getCreatedAt())
                .build();
    }

    /** 특정 유저 정보 조회 */
    public MemberInfoDTO getUserInfoByEmail(String email) {
        Member member = findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("해당 계정 없음"));

        return MemberInfoDTO.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .profileImage(member.getProfileImage())
                .createdAt(member.getCreatedAt())
                .role(member.getRole().name())
                .build();
    }


    /**
     * 로그인
     * refreshToken 쿠키나 httpOnly로 저장해야함
     * @param loginRequestDTO
     * @return
     */
    public LoginResponseDTO login(final LoginRequestDTO loginRequestDTO) {

        Member userInfo = findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new InvalidRequestException("등록된 계정이 없습니다"));

        // password 일치 여부 체크
        if(!bCryptPasswordEncoder.matches(loginRequestDTO.getPassword(), userInfo.getPassword())) {
            throw new InvalidRequestException("비밀번호 틀림");
        }

        // jwt 토큰 생성œœ
        String accessToken = jwtProvider.generateAccessToken(userInfo.getId());

        // 기존에 가지고 있는 사용자의 refresh token 제거
        RefreshToken.removeUserRefreshToken(userInfo.getId());

        // refresh token 생성 후 저장
        String refreshToken = jwtProvider.generateRefreshToken(userInfo.getId());

        RefreshToken.putRefreshToken(refreshToken, userInfo.getId());

        return LoginResponseDTO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
    }

    /** 내정보 업데이트 */
    public MemberUpdateDto updateMemberInfo(Long id, MemberUpdateRequest request) {
        Member member = findMemberById(id);
        member.update(request);

        return MemberUpdateDto.builder()
                .email(member.getEmail())
                .username(member.getUsername())
                .profileImage(member.getProfileImage())
                .createdAt(member.getCreatedAt())
                .updateAt(member.getUpdatedAt())
                .build();
    }

    /** 회원탈퇴 */
    public Member deleteMember(Long id) {

        Member member = findMemberById(id);

        /** 맴버 delete 상태 변경*/
        member.delete();

        /** 내가 속한 카테고리 조회*/
        List<RoomMember> includeRoomList = roomService.getIncludeRoomsByMemberId(member.getEmail(), InviteState.INVITE);

        /**
         * 일단 내가속한 방들의 inviteState를 GETOUT으로 모두 변경
         * 그런데, 내가 방장이면 RoomState를 DELETE로 업데이트
         */
        includeRoomList.forEach(roomMember -> {
            if(roomMember.getRoomMemberRole() == RoomMemberRole.OWNER) { // 본인이 만든거면 카테고리 삭제
                roomService.deleteMyRoom(member.getId(), roomMember.getRoom().getId(),null);
            } else { // 다른 카테고리에 속한거라면 나가기 처리
                roomService.getOutRoom(member.getId(), roomMember.getRoom().getId());
            }
        });

        return member;
    }

    /***
     * 이메일로 회원 조회
     * 옵셔널을 리턴하도록 해야함.
     * 회원가입할때 null 이면 가입이 가능하도록 만들고, null이 아니면 중복된 계정으로 함.
     * 저기서 써야해서 리턴값이 optional 이도록 해야함
     * @param email
     * @return
     */
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmailAndDeletedAtIsNull(email);
    }

    public Member findMemberById(Long id) {
        return memberRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new InvalidRequestException("해당 계정 없음"));
    }



//    public LoginResponseDTO refreshToken(String refreshToken) {
//
//    }
}
