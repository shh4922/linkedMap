package com.hyeonho.linkedmap.service;

import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.request.CreateCategoryReq;
import com.hyeonho.linkedmap.data.request.DeleteCategoryReq;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.CategoryState;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.enumlist.CategoryUserRole;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.error.DatabaseException;
import com.hyeonho.linkedmap.error.DuplicateMemberException;
import com.hyeonho.linkedmap.repository.CategoryRepository;
import com.hyeonho.linkedmap.repository.CategoryUserRepository;
import com.hyeonho.linkedmap.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
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
    public Optional<Category> createCategory(String email, CreateCategoryReq request) {
        try {
            Member member = memberRepository.findById(email)
                    .orElseThrow(() -> new RuntimeException("해당 계정 없음"));

            Category category = new Category(member, request.getCategoryName());
            saveCategory(category);

            // 카테고리 생성후, 카테고리유저 테이블에 추가
            CategoryUser categoryUser = new CategoryUser(category, member, InviteState.INVITE, CategoryUserRole.OWNER, CategoryState.ACTIVE);
            categoryUserRepository.save(categoryUser);
            return Optional.of(category);
        } catch (DatabaseException e) {
            throw new DatabaseException("카테고리 생성 에러");
        }
    }

    public Optional<Category> saveCategory(Category category) {
        return Optional.of(categoryRepository.save(category));
    }

    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow();
    }


    public List<Category> getIncludeCategoryByEmail(String email) {
        return categoryUserRepository.getIncludeCategoryByEmail(email);
    }


    @Transactional
    public Category deleteCategory(String email, Long categoryId) {
        try {
            Category category = findCategoryById(categoryId);

            if(!category.getOwner().getEmail().equals(email)) {
                ResponseEntity.badRequest()
                        .body(DefaultResponse.error(400, "카테고리 소유자 아니면 삭제못함"));
            }

            category.delete();

            // TODO: 삭제 성공후 카테고리유저에 있는 해당 카테고리에 속한 유저의 카테고리 상태를 DELETE로 업데이트 해줘야함. 벌크연산필요.
            if(saveCategory(category).isPresent()) {
                categoryUserRepository.updateCategoryStatusToDelete(categoryId,CategoryState.DELETE.name()); // categoryUser에 있는 해당카테고리 상태 삭제됌 으로 변경
                getOutCategory(email,categoryId);
//                categoryUserRepository.updateInviteStatusToDelete(categoryId,email,InviteState.GETOUT.name()); // 삭제한 유저 초대상태 나감으로 변경
            }

            return category;
        } catch (DatabaseException e) {
            throw new DatabaseException("카테고리 삭제 에러");
        }
    }

    @Transactional
    public int getOutCategory(String email, Long categoryId) {
        try {
            // 삭제한 유저 초대상태 나감으로 변경
            return categoryUserRepository.updateInviteStatusToDelete(categoryId,email,InviteState.GETOUT.name());
        } catch (DatabaseException e) {
            throw new DatabaseException("카테고리 나가기 에러");
        }
    }


}
