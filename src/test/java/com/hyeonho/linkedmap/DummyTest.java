package com.hyeonho.linkedmap;

import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.repository.CategoryRepository;
import com.hyeonho.linkedmap.repository.CategoryUserRepository;
import com.hyeonho.linkedmap.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class DummyTest {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryUserRepository categoryUserRepository;
    @Autowired
    public DummyTest(MemberRepository memberRepository, CategoryRepository categoryRepository, CategoryUserRepository categoryUserRepository) {
        this.memberRepository = memberRepository;
        this.categoryRepository = categoryRepository;
        this.categoryUserRepository = categoryUserRepository;
    }

    @Test
    public void 유저생성() {
        Member member = new Member();
        member.setEmail("test@test.com");
        member.setPassword("111111");
        member.setUsername("현호");


        Member member2 = new Member();
        member2.setEmail("test2@test.com");
        member2.setPassword("111111");
        member2.setUsername("지민");

        memberRepository.save(member);
        memberRepository.save(member2);
    }

    @Test
    public void 카테고리추가() {
        Member findMember = memberRepository.findById("test@test.com").orElseThrow();

        Category category = new Category();
        category.setName("빵맛집");
        category.setOwner(findMember.getEmail());
        Category savedCategory = categoryRepository.save(category);

        if (savedCategory != null && savedCategory.getId() != null) {
            CategoryUser categoryUser = new CategoryUser();
            categoryUser.setOwner(findMember);
            categoryUser.setCategory(savedCategory);
            categoryUser.setMember(findMember);

            categoryUserRepository.save(categoryUser);
        }
    }

    @Test
    public void 카테고리에_유저추가() {
        Member member = new Member();
        member.setEmail("tes3@test.com");
        member.setPassword("33333");
        member.setUsername("강원양");


        Member member2 = new Member();
        member2.setEmail("test4@test.com");
        member2.setPassword("44444");
        member2.setUsername("강원양2");

        memberRepository.save(member);
        memberRepository.save(member2);

        Category findCategory = categoryRepository.findById(1L).orElseThrow();
        Member Onwer = memberRepository.findById(findCategory.getOwner()).orElseThrow();

        CategoryUser categoryUser = new CategoryUser();
        categoryUser.setMember(member);
        categoryUser.setCategory(findCategory);
        categoryUser.setOwner(Onwer);

        CategoryUser categoryUser2 = new CategoryUser();
        categoryUser2.setMember(member2);
        categoryUser2.setCategory(findCategory);
        categoryUser2.setOwner(Onwer);

        categoryUserRepository.save(categoryUser);
        categoryUserRepository.save(categoryUser2);
    }


}
