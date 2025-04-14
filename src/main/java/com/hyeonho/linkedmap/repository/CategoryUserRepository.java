package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.CategoryState;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.InviteState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

//@NoRepositoryBean
public interface CategoryUserRepository extends JpaRepository<CategoryUser, Long> {



    /** 특정 유저가 속한 카테고리 리스트 조회. */
    @Query("SELECT cu FROM CategoryUser cu WHERE cu.member.email = :email AND cu.inviteState = :inviteState")
    List<CategoryUser> getIncludeCategoryByEmail(@Param(value = "email") String email, @Param(value = "inviteState") InviteState inviteState);


    /** 카테고리 유저의 카테고리상태를 변경 (삭제됨. 활성화됨 등등..) */
    @Modifying
    @Query("UPDATE CategoryUser cu SET cu.categoryState = :categoryState WHERE cu.category.id = :categoryId")
    int updateCategoryStatusToDelete(@Param("categoryState") CategoryState categoryState, @Param("categoryId") Long categoryId);


     /** 유저의 초대상태를 변경 */
    @Modifying
    @Query("UPDATE CategoryUser cu SET cu.inviteState = :inviteState WHERE cu.category.id = :categoryId AND cu.member.email = :email")
    int updateInviteStatusToDelete(@Param("inviteState") InviteState inviteState, @Param("categoryId") Long categoryId, @Param("email") String email);


    /**  */
    boolean existsByCategoryIdAndMemberEmail(Long categoryId, String email);


    /** 카테고리 유저*/
    @Query("SELECT cu FROM CategoryUser cu WHERE cu.member.email = :email AND cu.category.id = :categoryId AND cu.categoryState = :categoryState")
    Optional<CategoryUser> getCategoryUserByEmailAndCategoryId(@Param(value = "email") String email, @Param(value = "categoryId") Long categoryId, @Param(value = "categoryState") CategoryState categoryState);
}
