package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.RoomState;
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



    /** 특정 유저가 속한 카테고리 리스트 조회. */
    @Query("SELECT rm FROM RoomMember rm WHERE rm.member.email = :email AND rm.inviteState = :inviteState")
    List<RoomMember> getIncludeRoomByEmail(@Param(value = "email") String email, @Param(value = "inviteState") InviteState inviteState);

    /** 특정 카테고리 속한 카테고리유저 리스트 조회. */
    @Query("SELECT rm FROM RoomMember rm WHERE rm.room.id = :categoryId AND rm.inviteState = :inviteState")
    List<RoomMember> getIncludeCategoryByRoomId(@Param(value = "roomId") Long categoryId, @Param(value = "inviteState") InviteState inviteState);


    /** 카테고리 유저의 카테고리상태를 변경 (삭제됨. 활성화됨 등등..) */
    @Modifying
    @Query("UPDATE RoomMember rm SET rm.room.roomState = :roomState WHERE rm.room.id = :categoryId")
    int updateRoomStateToDelete(@Param("roomState") RoomState roomState, @Param("roomId") Long roomId);


     /** 유저의 초대상태를 변경 */
    @Modifying
    @Query("UPDATE RoomMember rm SET rm.inviteState = :inviteState WHERE rm.room.id = :roomId AND rm.member.email = :email")
    int updateInviteStatusToDelete(@Param("inviteState") InviteState inviteState, @Param("roomId") Long roomId, @Param("email") String email);


    /**  */
    boolean existsByCategoryIdAndMemberEmail(Long categoryId, String email);


    /** 카테고리 유저*/
    @Query("SELECT rm FROM RoomMember rm WHERE rm.member.email = :email AND rm.room.id = :roomId AND rm.room.roomState = :roomState")
    Optional<RoomMember> getCategoryUserByEmailAndRoomId(@Param(value = "email") String email, @Param(value = "roomId") Long categoryId, @Param(value = "roomState") RoomState roomState);

    /** 카테고리에 속한 유저수*/
    Long countByRoomIdAndInviteState(Long roomId, InviteState inviteState);
}
