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
    public ResponseEntity<DefaultResponse<Category>> createCategory(@RequestHeader HttpHeaders headers, @RequestBody CreateCategoryReq request) {
        if(request.getCategoryName().isEmpty()) { throw new InvalidRequestException("파라미터 비어있음");}

        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        String email = jwtProvider.getUsernameFromToken(authorization);

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
    public ResponseEntity<DefaultResponse<List<CategoryUser>>> getMyCategory(@RequestHeader HttpHeaders headers) {
        String email = jwtProvider.getEmailFromHeaders(headers);
        List<CategoryUser> categoryList = getShowableCategoryList(getCategoryList(email));
        return ResponseEntity.ok(DefaultResponse.success(categoryList));
    }


    /**
     * 특정 유저가 속한 카테고리 조회
     * 특정 유저가 속한 카테고리를 조회할때는, CategoryState 가 Active 인것만 조회해서 보여주어야함.
     */
    @GetMapping("category/include")
    public ResponseEntity<DefaultResponse<List<CategoryUser>>> getIncludeCategory(@RequestParam String email) {
        if(email.isEmpty()) { throw new InvalidRequestException("이메일이 없음"); }

        List<CategoryUser> categoryList = getShowableCategoryList(getCategoryList(email))
                .stream()
                .filter(categoryUser -> categoryUser.getCategoryState() == CategoryState.ACTIVE)
                .filter(categoryUser -> categoryUser.getInviteState() == InviteState.INVITE)
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
    public ResponseEntity<DefaultResponse<Category>> deleteCategory(@RequestHeader HttpHeaders headers, @RequestBody DeleteCategoryReq req) {
        String email = jwtProvider.getEmailFromHeaders(headers);
        if(req.getCategoryId() == null) {
            return ResponseEntity.badRequest()
                    .body(DefaultResponse.error(400, "카테고리 소유자 아니면 삭제못함"));
        }

        return ResponseEntity.ok(DefaultResponse.success(categoryService.deleteCategory(email, req.getCategoryId())));
    }

    /**
     * 카테고리 나가기.
     */
    @PostMapping("/category/getout/{categoryId}")
    public ResponseEntity<DefaultResponse<Integer>> getOutCategory(@RequestHeader HttpHeaders headers, @PathVariable Long categoryId) {
        if(categoryId == null) {
            return ResponseEntity.badRequest()
                    .body(DefaultResponse.error(400, "categoryId 가 없음"));
        }

        String email = jwtProvider.getEmailFromHeaders(headers);
        int result = categoryService.getOutCategory(email,categoryId);
        return ResponseEntity.ok(DefaultResponse.success(result));
    }


    /**
     * 카테고리 생성자인지 체크후, 수정
     * @param req
     * @return
     */
    @PutMapping("/category/update")
    public ResponseEntity<DefaultResponse<Category>> updateCategory(@RequestBody CategoryUpdateReq req) {
        Category category = categoryService.findCategoryById(req.getCategoryId());
        if(category.getOwner().equals(req.getMemberEmail())) {
            category.update(req);

            Category category1 = categoryService.saveCategory(category).orElseThrow();
            return ResponseEntity.ok(DefaultResponse.success(category1));
        }
        return ResponseEntity.badRequest().body(DefaultResponse.error(400,"권한이 없습니다"));
    }


    private List<CategoryUser> getCategoryList(String email) {
        return categoryService.getIncludeCategoryByEmail(email);
    }

    /** 삭제되지 않은 데이터만 리턴*/
    private List<CategoryUser> getShowableCategoryList(List<CategoryUser> categoryList) {
        return categoryList
                .stream()
                .filter(category -> category.getCategoryState() == CategoryState.ACTIVE)
                .toList();
    }
}
