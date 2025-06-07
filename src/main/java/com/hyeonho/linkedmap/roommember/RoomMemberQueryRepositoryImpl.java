package com.hyeonho.linkedmap.roommember;

import com.hyeonho.linkedmap.invite.InviteState;
import com.hyeonho.linkedmap.marker.QMarker;
import com.hyeonho.linkedmap.room.data.RoomDetailDTO;
import com.hyeonho.linkedmap.room.data.RoomListDTO;
import com.hyeonho.linkedmap.room.entity.QRoom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RoomMemberQueryRepositoryImpl implements RoomMemberQueryRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public List<RoomListDTO> findMyRoomListWithCounts(Long memberId) {
        QRoomMember rm = QRoomMember.roomMember;
        QRoom room = QRoom.room;
        QMarker marker = QMarker.marker;

        // room. ... 되어있는거 rm.room. 으로 바꾸고 밑에 join 지워도 상관없음.
        return queryFactory
                .select(Projections.constructor(RoomListDTO.class,
                        rm.id,
                        room.id,
                        room.name,
                        room.description,

                        room.currentOwner.id,
                        room.currentOwner.email,
                        room.currentOwner.username,
//                        room.creator.id,
                        room.creator.email,
                        room.creator.username,
                        room.roomState,

                        rm.inviteState,
                        rm.roomMemberRole,

                        JPAExpressions.select(marker.count())
                                .from(marker)
                                .where(marker.room.id.eq(room.id).and(marker.deletedAt.isNull())),

                        JPAExpressions.select(rm.count())
                                .from(rm)
                                .where(rm.room.id.eq(room.id).and(rm.inviteState.eq(InviteState.INVITE)))
                        ))
                .from(rm)
                .join(rm.room, room) // default inner join
                .where(rm.member.id.eq(memberId).and(rm.inviteState.eq(InviteState.INVITE)))
                .fetch();
    }

//    @Override
//    public RoomDetailDTO2 findRoomDetailById(Long id) {
//        return null;
//    }

    @Override
    public RoomDetailDTO findRoomDetailById(Long memberId, Long roomId) {
        QRoomMember rm = QRoomMember.roomMember;
        QRoom room = QRoom.room;
        QMarker marker = QMarker.marker;

        return queryFactory
                .select(Projections.constructor(RoomDetailDTO.class,
                        room.id,
                        room.name,
                        room.description,
                        room.imageUrl,
                        rm.roomMemberRole,

                        room.currentOwner.id,
                        room.currentOwner.username,
                        room.currentOwner.email,
                        room.creator.email,
                        room.creator.username,

                        room.createdAt,

                        JPAExpressions.select(marker.count())
                                .from(marker)
                                .where(marker.room.id.eq(room.id).and(marker.deletedAt.isNull()))
                        ))
                .from(room)
                .leftJoin(rm).on(rm.room.eq(room)
                                .and(rm.member.id.eq(memberId)) // 없으면 null 로 조인됨
                                .and(rm.inviteState.eq(InviteState.INVITE)))
                .where(room.id.eq(roomId))
                .fetchOne();
    }
}
