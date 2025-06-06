package com.hyeonho.linkedmap.member;

import com.hyeonho.linkedmap.auth.JWTProvider;
import com.hyeonho.linkedmap.auth.RefreshToken;
import com.hyeonho.linkedmap.invite.InviteState;
import com.hyeonho.linkedmap.member.member.LoginResponseDTO;
import com.hyeonho.linkedmap.member.member.MemberInfoDTO;
import com.hyeonho.linkedmap.member.member.MemberUpdateDto;
import com.hyeonho.linkedmap.member.member.RegisterDTO;
import com.hyeonho.linkedmap.data.request.LoginRequestDTO;
import com.hyeonho.linkedmap.data.request.MemberUpdateRequest;
import com.hyeonho.linkedmap.data.request.RegisterRequest;
import com.hyeonho.linkedmap.room.RoomState;
import com.hyeonho.linkedmap.enumlist.Role;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.DuplicateMemberException;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.room.service.RoomService;
import com.hyeonho.linkedmap.roommember.RoomMemberRepository;
import com.hyeonho.linkedmap.roommember.RoomMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final RoomMemberRepository roomMemberRepository;
    private final MemberRepository memberRepository;

    private final RoomService roomService;

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

    public MemberInfoDTO getMemberInfo(Long memberId) {
        Member member = findMemberById(memberId);
        return MemberInfoDTO.fromEntity(member);
    }

    /** 특정 유저 정보 조회 */
    public MemberInfoDTO getMemberInfoByEmail(String email) {
        Member member = findByEmail(email)
                .orElseThrow(() -> new InvalidRequestException("해당 유저 없음"));

        return MemberInfoDTO.fromEntity(member);
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

        // jwt 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(userInfo.getId(), userInfo.getRole());

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

    public String logout(Long memberId) {
        if(RefreshToken.removeUserRefreshToken(memberId)) {
            return "0";
        }
        return "-1";
    }

    /** 내정보 업데이트 */
    public MemberUpdateDto updateMemberInfo(Long memberId, MemberUpdateRequest request) {
        Member member = findMemberById(memberId);
        member.update(request);
        return MemberUpdateDto.fromEntity(member);
    }

    /** 회원탈퇴
     * 내가 속해있는 모든 방의 inviteState를 GETOUT으로 변경
     * 내가 방장인 방은 roomState를 DELETE로 변경
     * update Room set roomState = DELETE where id in (내가 방장인 방들의 id)
     * */
    public Member deleteMember(Long memberId) {
        // 내 정보 삭제
        Member member = findMemberById(memberId);
        member.delete();

        // 내가 만든 방 삭제
        roomService.deleteRoomByMemberId(memberId);

        // 내가 속한 모든 방 나가기
        roomMemberRepository.updateAllInviteStatusByMemberId(InviteState.GETOUT,memberId);

        return member;
    }

    /***
     * 이메일로 회원 조회
     * 옵셔널을 리턴하도록 해야함.
     * 회원가입할때 null 이면 가입이 가능하도록 만들고, null이 아니면 중복된 계정으로 함.
     * 저기서 써야해서 리턴값이 optional 이도록 해야함
     */
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmailAndDeletedAtIsNull(email);
    }

    public Member findMemberById(Long id) {
        return memberRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new InvalidRequestException("해당 계정 없음"));
    }

}

