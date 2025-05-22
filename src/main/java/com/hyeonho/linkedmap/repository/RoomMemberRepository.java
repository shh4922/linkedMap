package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.enumlist.RoomMemberRole;
import com.hyeonho.linkedmap.enumlist.RoomState;
import com.hyeonho.linkedmap.entity.RoomMember;
import com.hyeonho.linkedmap.enumlist.InviteState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

//@NoRepositoryBean
public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    RoomMember findByRoomIdAndMemberIdAndInviteState(Long roomId, Long memberId, InviteState inviteState);

    /** 특정 유저가 속한 카테고리 리스트 조회. */
    @Query("SELECT rm FROM RoomMember rm WHERE rm.member.id = :memberId AND rm.inviteState = :inviteState")
    List<RoomMember> getIncludeRoomByMemberId(@Param(value = "memberId") Long memberId, @Param(value = "inviteState") InviteState inviteState);

    /** 특정 카테고리 속한 카테고리유저 리스트 조회. */
    @Query("SELECT rm FROM RoomMember rm WHERE rm.room.id = :roomId AND rm.inviteState = :inviteState")
    List<RoomMember> getIncludeCategoryByRoomId(@Param(value = "roomId") Long roomId, @Param(value = "inviteState") InviteState inviteState);


    /** 카테고리 유저의 카테고리상태를 변경 (삭제됨. 활성화됨 등등..) */
    @Modifying
    @Query("UPDATE RoomMember rm SET rm.room.roomState = :roomState WHERE rm.room.id = :roomId")
    int updateRoomStateToDelete(@Param("roomState") RoomState roomState, @Param("roomId") Long roomId);


     /** 유저의 초대상태를 변경 */
    @Modifying
    @Query("UPDATE RoomMember rm SET rm.inviteState = :inviteState WHERE rm.room.id = :roomId AND rm.member.id = :memberId")
    int updateInviteStatusToDelete(@Param("inviteState") InviteState inviteState, @Param("roomId") Long roomId, @Param("memberId") Long memberId);


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
