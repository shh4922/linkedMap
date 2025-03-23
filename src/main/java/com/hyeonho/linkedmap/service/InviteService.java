package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.entity.Invite;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.repository.InviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;

    public Invite createInvite(String email, Long categoryId) {
        try {
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
