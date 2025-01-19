package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryUserRepository extends JpaRepository<CategoryUser, Long> {

    @Query("SELECT cu.member FROM CategoryUser cu WHERE cu.category.id = :categoryId")
    List<Member> findMembersByCategoryId(Long categoryId);
}
