package com.hyeonho.linkedmap.roommember;

import com.hyeonho.linkedmap.room.data.RoomDetailDTO;
import com.hyeonho.linkedmap.room.data.RoomListDTO;

import java.util.List;


public interface RoomMemberQueryRepository {

    List<RoomListDTO> findMyRoomListWithCounts(Long memberId);

    RoomDetailDTO findRoomDetailById(Long memberId, Long roomId);
}
