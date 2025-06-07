package com.hyeonho.linkedmap.roommember;

import com.hyeonho.linkedmap.room.RoomState;
import com.hyeonho.linkedmap.invite.InviteState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
//, RoomMemberQueryRepository
public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    RoomMember findByRoomIdAndMemberId(Long roomId, Long memberId);



    /** 특정 member 가 속한 roomMember 리스트 조회. */
    @Query("SELECT rm FROM RoomMember rm join fetch rm.room WHERE rm.member.id = :memberId AND rm.inviteState = :inviteState")
    List<RoomMember> getRoomMemberListByMemberId(@Param(value = "memberId") Long memberId, @Param(value = "inviteState") InviteState inviteState);


    /**
     * Using Fetch Join
     * 특정 방에 속한 roomMember 리스트 조회. */
    @Query("SELECT rm FROM RoomMember rm join fetch rm.member where rm.room.id = :roomId and rm.inviteState = :inviteState")
    List<RoomMember> getRoomMemberListByRoomId(@Param(value = "roomId") Long roomId, @Param(value = "inviteState") InviteState inviteState);



    /** 카테고리 유저의 방 를 변경 (삭제됨. 활성화됨 등등..) */
    @Modifying
    @Query("UPDATE RoomMember rm SET rm.room.roomState = :roomState WHERE rm.room.id in :roomIds")
    int updateRoomListState(@Param("roomState") RoomState roomState, @Param("roomIds") List<Long> roomIds);


     /** 유저의 초대상태를 변경 */
    @Modifying
    @Query("UPDATE RoomMember rm SET rm.inviteState = :inviteState WHERE rm.room.id = :roomId AND rm.member.id in :memberIds")
    int updateInviteStatusToDelete(@Param("inviteState") InviteState inviteState, @Param("roomId") Long roomId, @Param("memberId") List<Long> memberIds);

    /** 특정 유저가 속한 방의 상태를 변경 */
    @Modifying
    @Query("UPDATE RoomMember rm SET rm.inviteState = :roomState WHERE rm.member.id = :memberId")
    int updateAllInviteStatusByMemberId(@Param("inviteState") InviteState inviteState, @Param("memberId") Long memberId);


    /** 특정 유저가 속한 그룹리스트 전체 초대상태 변경 */
    @Modifying
    @Query("UPDATE RoomMember rm SET rm.inviteState = :inviteState WHERE rm.member.id = :memberId")
    int updateAllInviteStateByMemberId(@Param("inviteState") InviteState inviteState, @Param("memberId") Long memberId);


    /** 유저의 권한을 변경*/
    @Modifying
    @Query("UPDATE RoomMember rm SET rm.roomMemberRole = :roomMemberRole WHERE rm.id = :id")
    int updateRoomMemberRoleById(@Param("roomMemberRole") RoomMemberRole roomMemberRole, @Param("id") Long id);


    /** RoomMember 유저*/
    @Query("SELECT rm FROM RoomMember rm WHERE rm.member.id = :memberId AND rm.room.id = :roomId AND rm.room.roomState = :roomState")
    Optional<RoomMember> getRoomMemberByMemberIdAndRoomId(@Param(value = "memberId") Long memberId, @Param(value = "roomId") Long roomId, @Param(value = "roomState") RoomState roomState);



    /** 카테고리에 속한 유저수*/
    Long countByRoomIdAndInviteState(Long roomId, InviteState inviteState);
}
