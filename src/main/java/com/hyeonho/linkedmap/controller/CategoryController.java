package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.data.request.CreateCategoryReq;
import com.hyeonho.linkedmap.data.request.DeleteCategoryReq;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.repository.MemberRepository;
import com.hyeonho.linkedmap.service.CategoryService;
import com.hyeonho.linkedmap.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** 특정유저가 속한, 카테고리 조회 */
    @GetMapping("categoys/include")
    public List<Category> getIncludeCategory(@RequestParam Map<String, String> req) {
        String email = req.get("email");
        if(email.isEmpty()) {
            System.out.println("이메일 비어있음");
        }
        return categoryService.getIncludeCategory(email);
    }

    /** 카테고리 생성 */
    @PostMapping("/category/create")
    public Category createCategory(@RequestBody CreateCategoryReq request) {
        return categoryService.createCategory(request);
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


}
