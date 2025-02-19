package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.data.DefaultResponse;
import com.hyeonho.linkedmap.data.dto.MarkerRes;
import com.hyeonho.linkedmap.data.request.CategoryUpdateReq;
import com.hyeonho.linkedmap.data.request.CreateCategoryReq;
import com.hyeonho.linkedmap.data.request.DeleteCategoryReq;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** 특정유저가 속한, 카테고리 조회 */
    @GetMapping("categoys/include")
    public ResponseEntity<DefaultResponse<List<Category>>> getIncludeCategory(@RequestParam Map<String, String> req) {
        String email = req.get("email");
        if(email.isEmpty()) {
            return ResponseEntity.badRequest().body(DefaultResponse.error(400, "이메일이 없습니다."));
        }
        List<Category> categoryList = categoryService.getIncludeCategory(email);
        return ResponseEntity.ok(DefaultResponse.success(categoryList));
    }

    /** 카테고리 생성 */
    @PostMapping("/category/create")
    public ResponseEntity<DefaultResponse<Category>> createCategory(@RequestBody CreateCategoryReq request) {
        Optional<Category> categoryOptional = categoryService.createCategory(request);

        if (categoryOptional.isPresent()) {
            return ResponseEntity.ok(DefaultResponse.success(categoryOptional.get()));
        } else {
            return ResponseEntity.badRequest()
                    .body(DefaultResponse.error(400, "카테고리 생성 실패"));
        }
    }

    /**
     * 카테고리 삭제
     * 카테고리 삭제시, CategoryUser에 있는 곳에 deleted_at 시간 또한 업데이트 해주어야함.
     * @param req
     * @return
     */
    @PostMapping("/category/delete")
    public Category deleteCategory(@RequestBody DeleteCategoryReq req) {
        return categoryService.deleteCategory(req);
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
            Category category1 = categoryService.saveCategory(category);
            return ResponseEntity.ok(DefaultResponse.success(category1));
        }
        return ResponseEntity.badRequest().body(DefaultResponse.error(400,"권한이 없습니다"));
    }
}
