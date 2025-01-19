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
    public void createMemberInviteUser() {
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
    public void createCategory() {
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

        Member findMember = memberRepository.findById(1L).orElseThrow();

        Category category = new Category();
        category.setName("빵맛집");
        category.setOwner(findMember.getId());
        categoryRepository.save(category);
    }

    @Test
    public void createCategoryUser() {
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

        Member findMember = memberRepository.findById(1L).orElseThrow();
        Category category = new Category();
        category.setName("빵맛집");
        category.setOwner(findMember.getId());
        categoryRepository.save(category);

        CategoryUser categoryUser = new CategoryUser();
        categoryUser.setOwner(findMember);
        categoryUser.setCategory(category);
        categoryUser.setOwner(findMember);
        categoryUser.setMember(findMember);
        categoryUserRepository.save(categoryUser);

        memberRepository.delete(findMember);

    }
}
