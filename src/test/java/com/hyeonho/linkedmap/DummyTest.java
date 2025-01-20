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

    /**
     * 외래키 안걸고 테스트할때
     * 일단 삭제가 가능하긴한데, 이러면 다른테이블에 있는것도 null 하거나, delete_at에 다 추가해줘야하는거 아님?
     * 그럼 그거대로 일 아닌가 싶음
     */
    @Test
    public void createCategoryUserWithOutFK() {
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


    @Test
    public void createCategoryUserWithFK() {
        Member member = new Member();
        member.setEmail("test@test.com");
        member.setPassword("111111");
        member.setUsername("현호");

        Member member2 = new Member();
        member2.setEmail("test2@test.com");
        member2.setPassword("111111");
        member2.setUsername("지민");

        Member member3 = new Member();
        member3.setEmail("test3@test.com");
        member3.setPassword("111111");
        member3.setUsername("원규");

        Member member4 = new Member();
        member4.setEmail("tes42@test.com");
        member4.setPassword("111111");
        member4.setUsername("준호");

        memberRepository.save(member);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);

        Category category = new Category();
        category.setName("빵맛집");
        category.setOwner(member.getId());
        categoryRepository.save(category);

        CategoryUser categoryUser = new CategoryUser();
        categoryUser.setOwner(member);
        categoryUser.setCategory(category);
        categoryUser.setMember(member);

        CategoryUser categoryUser2 = new CategoryUser();
        categoryUser2.setOwner(member);
        categoryUser2.setCategory(category);
        categoryUser2.setMember(member2);

        CategoryUser categoryUser3 = new CategoryUser();
        categoryUser3.setOwner(member);
        categoryUser3.setCategory(category);
        categoryUser3.setMember(member3);

        CategoryUser categoryUser4 = new CategoryUser();
        categoryUser4.setOwner(member);
        categoryUser4.setCategory(category);
        categoryUser4.setMember(member4);

        categoryUserRepository.save(categoryUser);
        categoryUserRepository.save(categoryUser2);
        categoryUserRepository.save(categoryUser3);
        categoryUserRepository.save(categoryUser4);

        List<Member> memberList = categoryUserRepository.findMembersByCategoryId(category.getId());
    }

    @Test
    public void createCategoryAndSaveMember() {
        Member findMember = memberRepository.findById(2L).orElseThrow();
        Category category = new Category();
        category.setName("개쩌는 맛도리 집");
        category.setOwner(findMember.getId());
        categoryRepository.save(category);

        CategoryUser categoryUser = new CategoryUser();
        categoryUser.setCategory(category);
        categoryUser.setMember(findMember);
        categoryUser.setOwner(findMember);
        categoryUserRepository.save(categoryUser);

        Member findMember3 = memberRepository.findById(3L).orElseThrow();
        Member findMember4 = memberRepository.findById(4L).orElseThrow();

        CategoryUser categoryUser2 = new CategoryUser();
        categoryUser2.setCategory(category);
        categoryUser2.setMember(findMember3);
        categoryUser2.setOwner(findMember);
        categoryUserRepository.save(categoryUser2);

        CategoryUser categoryUser3 = new CategoryUser();
        categoryUser3.setCategory(category);
        categoryUser3.setMember(findMember4);
        categoryUser3.setOwner(findMember);
        categoryUserRepository.save(categoryUser3);

        List<Member> memberList = categoryUserRepository.findMembersByCategoryId(category.getId());
        System.out.println(memberList);
    }

    @Test
    public void 에드가뭔지테스트() {
        Member member = new Member();
        member.setEmail("개쩌는이메일@gmail.com");
        member.setPassword("yammy");
        member.setUsername("야미맨");
        memberRepository.save(member);

        Category category = categoryRepository.findById(1L).orElseThrow();
        List<Member> memberList = categoryUserRepository.findMembersByCategoryId(category.getId());
        System.out.println(memberList);
        memberList.add(member);
        System.out.println(memberList);
    }
}
