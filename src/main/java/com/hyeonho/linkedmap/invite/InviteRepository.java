package com.hyeonho.linkedmap.invite;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface InviteRepository extends JpaRepository<Invite, Long> {

    /** UUID 로 초대링크 찾기*/
    @Query("SELECT inv FROM Invite inv WHERE inv.inviteKey = :inviteKey")
    Invite findInviteByUUID(@Param("inviteKey")UUID inviteKey);


//    "UPDATE CategoryUser cu SET cu.categoryState = :state WHERE cu.category.id = :categoryId"

    @Transactional
    @Modifying
    @Query("UPDATE Invite inv SET inv.inviteState = :inviteState WHERE inv.inviteKey = :inviteKey")
    int updateInviteStateByUUID(InviteState inviteState, UUID inviteKey);

    @Transactional
    @Modifying
    @Query("UPDATE Invite inv SET inv.inviteState = :inviteState, inv.invitor = :memberId WHERE inv.inviteKey = :inviteKey")
    int updateInviteMemberByUUID(@Param("inviteState")InviteState inviteState, @Param("memberId") Long memberId, @Param("inviteKey")UUID inviteKey);
}
