package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.config.JWTProvider;
import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.request.CategoryUpdateReq;
import com.hyeonho.linkedmap.data.request.CreateCategoryReq;
import com.hyeonho.linkedmap.data.request.DeleteCategoryReq;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.CategoryState;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.enumlist.InviteState;
import com.hyeonho.linkedmap.error.InvalidRequestException;
import com.hyeonho.linkedmap.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final JWTProvider jwtProvider;


    /** 카테고리 생성 */
    @PostMapping("/category/create")
    public ResponseEntity<DefaultResponse<Category>> createCategory(@AuthenticationPrincipal String email, @RequestBody CreateCategoryReq request) {
        if(request.getCategoryName().isEmpty()) { throw new InvalidRequestException("파라미터 비어있음");}

        Optional<Category> categoryOptional = categoryService.createCategory(email,request);
        if (categoryOptional.isPresent()) {
            return ResponseEntity.ok(DefaultResponse.success(categoryOptional.get()));
        } else {
            return ResponseEntity.badRequest()
                    .body(DefaultResponse.error(400, "카테고리 생성 실패"));
        }
    }

    /**
     * 내가 속해있는 카테고리 조회
     * InviteState 가 Invited 인 것만 조회해야함.
     * 하지만 CategoryStatus 가 delete 면 화면에서 삭제된카테고리 라고 보여줘야함.
     * categoryUser를 조회해야함
     */
    @GetMapping("/category/me")
    public ResponseEntity<DefaultResponse<List<CategoryUser>>> getMyCategory(@AuthenticationPrincipal String email) {
        List<CategoryUser> categoryList = getCategoryList(email);
        return ResponseEntity.ok(DefaultResponse.success(categoryList));
    }


    /**
     * 특정 유저가 속한 카테고리 조회
     * InviteState = invite (초대된것만 보이게)
     * CategoryState = Active (활성화된 카테고리만 보이게)
     */
    @GetMapping("/category/include")
    public ResponseEntity<DefaultResponse<List<CategoryUser>>> getIncludeCategory(@RequestParam(value = "email") String email) {
        if(email.isEmpty()) { throw new InvalidRequestException("이메일이 없음"); }

        List<CategoryUser> categoryList = getCategoryList(email)
                .stream()
                .filter(categoryUser -> categoryUser.getCategoryState() == CategoryState.ACTIVE)
                .toList();

        return ResponseEntity.ok(DefaultResponse.success(categoryList));
    }




    /**
     * 카테고리 삭제 (카테고리 생성자가 해당카테고리를 삭제함)
     * 카테고리 삭제시, CategoryUser에 있는 곳에 deleted_at 시간 또한 업데이트 해주어야함.
     * 카테고리 유저에 있는 해당 카테고리의 category_state 를 Delete로 변경
     * @param req
     * @return
     */
    @DeleteMapping("/category")
    public ResponseEntity<DefaultResponse<Category>> deleteCategory(@AuthenticationPrincipal String email, @RequestBody DeleteCategoryReq req) {
        if(req.getCategoryId() == null) { throw new InvalidRequestException("카테고리 파라미터 없음"); }

        return ResponseEntity.ok(DefaultResponse.success(categoryService.deleteCategory(email, req.getCategoryId())));
    }

    /**
     * 카테고리 나가기.
     */
    @PostMapping("/category/getout/{categoryId}")
    public ResponseEntity<DefaultResponse<Integer>> getOutCategory(@AuthenticationPrincipal String email, @PathVariable("categoryId") Long categoryId) {
        if(categoryId == null) { throw new InvalidRequestException("카테고리 파라미터 없음");}

        int result = categoryService.getOutCategory(email,categoryId);
        return ResponseEntity.ok(DefaultResponse.success(result));
    }


    /**
     * 카테고리 생성자인지 체크후, 수정
     * @param req
     * @return
     */
    @PutMapping("/category/update")
    public ResponseEntity<DefaultResponse<Category>> updateCategory(@AuthenticationPrincipal String email, @RequestBody CategoryUpdateReq req) {
        return ResponseEntity.ok(DefaultResponse.success(categoryService.updateCategory(email,req)));
    }


    private List<CategoryUser> getCategoryList(String email) {
        return categoryService.getIncludeCategoryByEmail(email, InviteState.INVITE);
    }

}
