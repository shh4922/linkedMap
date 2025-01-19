package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
