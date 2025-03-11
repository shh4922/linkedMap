package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.CategoryState;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
    @Query("SELECT cu.category FROM CategoryUser cu WHERE cu.member.id = :email")
    List<Category> getIncludeCategoryByEmail(String email);

    /**
     * 카테고리id, 유저이름 으로 카테고리 찾기
     * @param email
     * @return
     */
    @Query("SELECT * FROM CategoryUser cu WHERE cu.member.id = :email AND cu.category.id = :categoryId")
    CategoryUser findCategoryUserByCategoryIdAndEmail(String email, Long categoryId);

    @Modifying
    @Query("UPDATE CategoryUser cu SET cu.categoryStatus = :status WHERE cu.category.id = :categoryId")
    int updateCategoryStatusToDelete(@Param("categoryId") Long categoryId, @Param("status") CategoryState status);


    boolean existsByCategoryIdAndMemberEmail(Long categoryId, String email);

}
