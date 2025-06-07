package com.hyeonho.linkedmap.room.repository;

import com.hyeonho.linkedmap.invite.InviteState;
import com.hyeonho.linkedmap.room.RoomState;
import com.hyeonho.linkedmap.room.entity.Room;
import com.hyeonho.linkedmap.roommember.RoomMemberQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByIdAndDeletedAtIsNull(Long categoryId);

    @Modifying
    @Query("UPDATE Room r SET r.roomState = :roomState WHERE r.id in :roomIds")
    int updateRoomStateByRoomId(@Param("roomState") RoomState roomState, @Param("roomIds") List<Long> roomIds);

    @Modifying
    @Query("UPDATE Room r SET r.roomState = :roomState WHERE r.currentOwner.id = :memberId")
    int updateRoomStateByMemberId(@Param("roomState") RoomState roomState, @Param("memberId") Long memberId);



}
