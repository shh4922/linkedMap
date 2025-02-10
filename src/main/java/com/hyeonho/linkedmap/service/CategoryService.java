package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.data.request.CreateCategoryReq;
import com.hyeonho.linkedmap.data.request.DeleteCategoryReq;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.CategoryUserRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.repository.CategoryRepository;
import com.hyeonho.linkedmap.repository.CategoryUserRepository;
import com.hyeonho.linkedmap.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final CategoryUserRepository categoryUserRepository;

    public CategoryService(CategoryRepository categoryRepository, MemberRepository memberRepository, CategoryUserRepository categoryUserRepository) {
        this.categoryRepository = categoryRepository;
        this.memberRepository = memberRepository;
        this.categoryUserRepository = categoryUserRepository;
    }

    /**
     * 카테고리 생성
     * 카테고리 생성시, 생성이 완료되면 CategoryUser에 invite상태로, OWNER 역할의 유저를 추가한다.
     * @param request
     * @return
     */
    public Category createCategory(CreateCategoryReq request) {
        Member member = memberRepository.findById(request.getEmail()).orElseThrow();
        Category category = new Category(member,request.getCategoryName());

        Category savedCategory = categoryRepository.save(category);

        if(savedCategory.equals(category)) {
            CategoryUser categoryUser = new CategoryUser(savedCategory, member, InviteState.INVITE, CategoryUserRole.OWNER);
            categoryUserRepository.save(categoryUser);
        }

        return savedCategory;
    }


    public List<Category> getIncludeCategory(String email) {
        return categoryUserRepository.getIncludeCategoryByEmail(email);
    }

    @Transactional
    public Category deleteCategory(DeleteCategoryReq req) {
        Category findCategory = categoryRepository.findById(req.getCategoryId()).orElseThrow();

        if (findCategory.getOwner().getEmail().equals(req.getEmail())) {
            findCategory.delete(); // deletedAt 필드를 현재 시간으로 업데이트
            Category category = categoryRepository.save(findCategory); // 변경된 엔티티 저장

            categoryUserRepository.bulkUpdateDeletedAtByCategoryId(category.getId(),category.getDeletedAt());
            return category;
        } else {
            throw new IllegalArgumentException("You are not the owner of this category");
        }
    }

}
