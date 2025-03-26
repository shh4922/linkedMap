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

import java.util.List;

//@NoRepositoryBean
public interface CategoryUserRepository extends JpaRepository<CategoryUser, Long> {

    /**
     * 특정 카테고리에 속한 유저리스트 리턴
     * @param categoryId
     * @return
     */
    @Query("SELECT cu.member FROM CategoryUser cu WHERE cu.category.id = :categoryId")
    List<Member> findMembersByCategoryId(Long categoryId);

    /**
     * 특정 유저가 속한 카테고리 리턴
     * @param email
     * @return
     */
    @Query("SELECT CategoryUser cu FROM cu WHERE cu.member.id = :email")
    List<CategoryUser> getIncludeCategoryByEmail(String email);


    @Modifying
    @Query("UPDATE CategoryUser cu SET cu.categoryState = :state WHERE cu.category.id = :categoryId")
    int updateCategoryStatusToDelete(Long categoryId,  String state);
//
    @Modifying
    @Query("UPDATE CategoryUser cu SET cu.inviteState = :inviteState WHERE cu.category.id = :categoryId AND cu.member.email = :email")
    int updateInviteStatusToDelete(Long categoryId, String email, String inviteState);



    boolean existsByCategoryIdAndMemberEmail(Long categoryId, String email);

}
