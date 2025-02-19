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
import java.util.Optional;

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
    public Optional<Category> createCategory(CreateCategoryReq request) {
        Member member = memberRepository.findById(request.getEmail()).orElseThrow();
        Category category = new Category(member, request.getCategoryName());
        Category savedCategory = saveCategory(category);


        if(savedCategory.equals(category)) {
            CategoryUser categoryUser = new CategoryUser(savedCategory, member, InviteState.INVITE, CategoryUserRole.OWNER);
            categoryUserRepository.save(categoryUser);
            return Optional.of(savedCategory);
        }
        return Optional.empty();
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow();
    }


    public List<Category> getIncludeCategory(String email) {
        return categoryUserRepository.getIncludeCategoryByEmail(email);
    }

    /**
     * 카테고리 삭제시, CategoryUser에 있는 카테고리도 모두 삭제해주어야함.
     * @param req
     * @return
     */
    @Transactional
    public Category deleteCategory(DeleteCategoryReq req) {
        Category findCategory = categoryRepository.findById(req.getCategoryId()).orElseThrow();

        if (findCategory.getOwner().getEmail().equals(req.getEmail())) {
            findCategory.delete(); // deletedAt 필드를 현재 시간으로 업데이트
            saveCategory(findCategory);

//            categoryUserRepository.bulkUpdateDeletedAtByCategoryId(category.getId(),category.getDeletedAt());
            return findCategory;
        } else {
            throw new IllegalArgumentException("You are not the owner of this category");
        }
    }


}
