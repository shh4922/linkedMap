package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    // 탈퇴하지 않은 유저 조회
    Optional<Member> findByEmailAndDeletedAtIsNull(String email);

    Optional<Member> findByIdAndDeletedAtIsNull(Long id);
}
