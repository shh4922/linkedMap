package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.entity.Room;
import com.hyeonho.linkedmap.entity.Invite;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.repository.RoomRepository;
import com.hyeonho.linkedmap.repository.InviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InviteService {

    private final InviteRepository inviteRepository;
    private final RoomRepository roomRepository;
    public Invite createInvite(String email, Long categoryId) {
        try {
            Room room = roomRepository.findByIdAndDeletedAtIsNull(categoryId);
            if(!room.getOwner().getEmail().equals(email)) {
                throw new InvalidRequestException("권한이 없습니다");
            }
            Invite invite = new Invite(categoryId, email);
            return inviteRepository.save(invite);
        }catch (DatabaseException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

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

    public int updateInviteMemberByUUID(UUID inviteKey, String email, InviteState inviteState) {
        try {
            return inviteRepository.updateInviteMemberByUUID(inviteState, email, inviteKey);
        } catch (DatabaseException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
}
