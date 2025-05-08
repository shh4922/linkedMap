package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.data.dto.invite.CreateInviteDTO;
import com.hyeonho.linkedmap.entity.Room;
import com.hyeonho.linkedmap.entity.Invite;
import com.hyeonho.linkedmap.entity.RoomMember;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.repository.RoomRepository;
import com.hyeonho.linkedmap.repository.InviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InviteService {

    private final InviteRepository inviteRepository;
    private final RoomRepository roomRepository;

    // TODO: 매니저도 링크를 만들수있게 해야할까?
    public CreateInviteDTO createInvite(Long memberId, Long roomId) {
        try {
            Room room = roomRepository.findByIdAndDeletedAtIsNull(roomId)
                    .orElseThrow(() -> new InvalidRequestException("Room not found"));

            if(!room.getCurrentOwner().getId().equals(memberId)) {
                throw new InvalidRequestException("권한이 없습니다");
            }

            Invite invite = Invite
                    .builder()
                    .roomId(roomId)
                    .invitor(memberId)
                    .build();
            Invite invite1 = inviteRepository.save(invite);

            String url = String.format("https://www.linkedmap.com/invite/%s/%s", invite1.getRoomId(), invite.getInviteKey());
            return new CreateInviteDTO(url);

        }catch (DatabaseException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    // TODO: Service에서 처리하도록 변경해야함.
//    public RoomMember joinRoom(Long roomId, Long memberId, UUID inviteKey) {
//
//    }

    public Invite findInviteByUUID(UUID inviteKey) {
        try {
            return inviteRepository.findInviteByUUID(inviteKey);
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public int updateInviteStateByUUID(UUID inviteKey, InviteState inviteState) {
        try {
            return inviteRepository.updateInviteStateByUUID(inviteState, inviteKey);
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    public int updateInviteMemberByUUID(UUID inviteKey, Long memberId, InviteState inviteState) {
        try {
            return inviteRepository.updateInviteMemberByUUID(inviteState, memberId, inviteKey);
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
