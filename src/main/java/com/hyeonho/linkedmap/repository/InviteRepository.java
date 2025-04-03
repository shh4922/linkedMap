package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Invite;
import com.hyeonho.linkedmap.enumlist.InviteState;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface InviteRepository extends JpaRepository<Invite, Long> {

    @Query("SELECT in FROM Invite in WHERE in.inviteKey = :inviteKey")
    Invite findInviteByUUID(@Param("inviteKey")UUID inviteKey);


//    "UPDATE CategoryUser cu SET cu.categoryState = :state WHERE cu.category.id = :categoryId"

    @Transactional
    @Modifying
    @Query("UPDATE Invite in SET in.inviteState = :inviteState WHERE in.inviteKey = :inviteKey")
    int updateInviteStateByUUID(InviteState inviteState, UUID inviteKey);

    @Transactional
    @Modifying
    @Query("UPDATE Invite in SET in.inviteState = :inviteState, in.invitedMember = :email WHERE in.inviteKey = :inviteKey")
    int updateInviteMemberByUUID(@Param("inviteState")InviteState inviteState, @Param("email")String email, @Param("inviteKey")UUID inviteKey);
}
