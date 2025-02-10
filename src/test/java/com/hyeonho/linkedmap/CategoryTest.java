package com.hyeonho.linkedmap;

import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.Member;
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
        Member member = memberRepository.findById("test111@test.com").orElseThrow();

        Category category = new Category(member,"카테고리1");
        categoryRepository.save(category);
    }

    @Test
    public void 유저가속한_카테고리_리스트조회() {
        List<Category> categoryList = categoryUserRepository.getIncludeCategoryByEmail("test111@test.com");
        log.info("categoryList", categoryList);
    }
}
