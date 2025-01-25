package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.data.request.CreateCategoryReq;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.repository.MemberRepository;
import com.hyeonho.linkedmap.service.CategoryService;
import com.hyeonho.linkedmap.service.MemberService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("categoys/include")
    public List<Category> getIncludeCategory(Map<String, String> req) {
        String email = req.get("email");
        if(email.isEmpty()) {
            System.out.println("이메일 비어있음");
        }
        return categoryService.getIncludeCategory(email);
    }

    @PostMapping("/category/create")
    public Category createCategory(CreateCategoryReq request) {
        return categoryService.createCategory(request);
    }


}
