package com.hyeonho.linkedmap.controller;

import com.hyeonho.linkedmap.data.request.CreateCategoryReq;
import com.hyeonho.linkedmap.data.request.DeleteCategoryReq;
import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


    /**
     * 카테고리 업데이트
     * @param req
     * @return
     */
    @PutMapping("/category/update")
    public Category updateCategory(@RequestBody DeleteCategoryReq req) {
        Category category = categoryService.findCategoryById(req.getCategoryId());

        BeanUtils.copyProperties(req, category, getNullPropertyNames(req));
        return categoryService.saveCategory(category);
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

}
