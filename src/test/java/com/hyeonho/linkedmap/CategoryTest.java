package com.hyeonho.linkedmap;

import com.hyeonho.linkedmap.data.request.CategoryUpdateReq;
import com.hyeonho.linkedmap.data.request.DeleteCategoryReq;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.CategoryUserRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.repository.CategoryRepository;
import com.hyeonho.linkedmap.repository.CategoryUserRepository;
import com.hyeonho.linkedmap.repository.MarkerRepository;
import com.hyeonho.linkedmap.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class CategoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryUserRepository categoryUserRepository;

    @Autowired
    private MarkerRepository markerRepository;
    private static final Logger log = LoggerFactory.getLogger(CategoryTest.class);


    @Test
    public void 카테고리생성() {
        Member member = memberRepository.findById("test222@test.com").orElseThrow();

        Category category = new Category(member,"카테고리2");
        categoryRepository.save(category);

        CategoryUser categoryUser = new CategoryUser(category, member, InviteState.INVITE, CategoryUserRole.OWNER);
        categoryUserRepository.save(categoryUser);
    }

    @Test
    public void 유저가속한_카테고리_리스트조회() {
        List<Category> categoryList = categoryUserRepository.getIncludeCategoryByEmail("test111@test.com");
        List<Category> filterdCategoryList = categoryList.stream()
                .filter(category -> category.getDeletedAt() == null)
                        .toList();
        log.info("filtered", filterdCategoryList);
    }

    @Test
    public void 카테고리업데이트() {
        CategoryUpdateReq categoryUpdateReq = new CategoryUpdateReq();
        categoryUpdateReq.setMemberEmail("test111@test.com");
        categoryUpdateReq.setCategoryId(1L);
        categoryUpdateReq.setCategoryName("업데이트한 카테고리2");

        Category category = categoryRepository.findById(categoryUpdateReq.getCategoryId()).orElseThrow();
        if(category.getOwner().getEmail().equals(categoryUpdateReq.getMemberEmail())) {
            category.update(categoryUpdateReq);
            categoryRepository.save(category);
        }
    }

    @Test
    public void 카테고리_삭제() {
        DeleteCategoryReq deleteCategoryReq = new DeleteCategoryReq();
        deleteCategoryReq.setEmail("test111@test.com");
        deleteCategoryReq.setCategoryId(2L);

        Category category = categoryRepository.findById(deleteCategoryReq.getCategoryId()).orElseThrow();
        if(category.getOwner().getEmail().equals(deleteCategoryReq.getEmail())) {
            category.delete();
            categoryRepository.save(category);

            // TODO: 카테고리유저Repo 에서 삭제하면 해당 카테고리Id 찾아서 다들 delete 삭제해주어야함.
        }
    }
}
