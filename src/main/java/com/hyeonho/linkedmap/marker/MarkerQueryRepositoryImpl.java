package com.hyeonho.linkedmap.marker;

import com.hyeonho.linkedmap.invite.InviteState;
import com.hyeonho.linkedmap.marker.marker.CreateMarkerDTO;
import com.hyeonho.linkedmap.room.RoomState;
import com.hyeonho.linkedmap.room.entity.QRoom;
import com.hyeonho.linkedmap.roommember.QRoomMember;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MarkerQueryRepositoryImpl implements MarkerQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CreateMarkerDTO> getMarkerList(Long memberId, Long roomId) {
        QRoomMember rm = QRoomMember.roomMember;
        QMarker marker = QMarker.marker;

        return queryFactory
                .select(Projections.constructor(CreateMarkerDTO.class,
                        marker.id,
                        marker.lat,
                        marker.lng,
                        marker.title,
                        marker.description,
                        marker.address,
                        marker.roadAddress,
                        marker.storeType,
                        marker.imageUrl,

                        marker.member.id,
                        marker.member.email, // 이렇게 하면 자동으로 조인 해줌.;;; 단 select 에서만 가능
                        marker.member.username, // 이렇게 하면 자동으로 조인 해줌.;;; 단 select 에서만 가능
                        marker.room.id,
                        marker.room.name,

                        marker.createdAt,
                        marker.updatedAt,

                        rm.roomMemberRole
                        ))
                .from(marker)
                .join(rm).on(
                        rm.room.id.eq(roomId)
                                .and(rm.member.id.eq(memberId)
                                        .and(rm.inviteState.eq(InviteState.INVITE))
                                ))
                .where(marker.room.id.eq(roomId).and(marker.deletedAt.isNull()))
                .fetch();
    }
}
