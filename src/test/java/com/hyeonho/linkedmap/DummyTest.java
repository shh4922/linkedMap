package com.hyeonho.linkedmap;

import com.hyeonho.linkedmap.entity.Category;
import com.hyeonho.linkedmap.entity.CategoryUser;
import com.hyeonho.linkedmap.entity.Marker;
import com.hyeonho.linkedmap.entity.Member;
import com.hyeonho.linkedmap.repository.CategoryRepository;
import com.hyeonho.linkedmap.repository.CategoryUserRepository;
import com.hyeonho.linkedmap.repository.MarkerRepository;
import com.hyeonho.linkedmap.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class DummyTest {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryUserRepository categoryUserRepository;
    private final MarkerRepository markerRepository;
    @Autowired
    public DummyTest(MemberRepository memberRepository, CategoryRepository categoryRepository, CategoryUserRepository categoryUserRepository, MarkerRepository markerRepository) {
        this.memberRepository = memberRepository;
        this.categoryRepository = categoryRepository;
        this.categoryUserRepository = categoryUserRepository;
        this.markerRepository = markerRepository;
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
        category.setOwner(findMember);
        Category savedCategory = categoryRepository.save(category);

        if (savedCategory != null && savedCategory.getId() != null) {
            CategoryUser categoryUser = new CategoryUser();
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

        CategoryUser categoryUser = new CategoryUser();
        categoryUser.setMember(member);
        categoryUser.setCategory(findCategory);

        CategoryUser categoryUser2 = new CategoryUser();
        categoryUser2.setMember(member2);
        categoryUser2.setCategory(findCategory);

        categoryUserRepository.save(categoryUser);
        categoryUserRepository.save(categoryUser2);
    }

    @Test
    public void 다른유저가_카테고리추가후_맴버추가하는과정() {
        Member findMember = memberRepository.findById("test2@test.com").orElseThrow();

        Category category = new Category();
        category.setName("디지게맛나는빵집");
        category.setOwner(findMember);
        Category savedCategory = categoryRepository.save(category);

        if (savedCategory != null && savedCategory.getId() != null) {
            CategoryUser categoryUser = new CategoryUser();
            categoryUser.setCategory(savedCategory);
            categoryUser.setMember(findMember);
            categoryUserRepository.save(categoryUser);
        }

        Member findMember2 = memberRepository.findById("tes3@test.com").orElseThrow();
        Member findMember3 = memberRepository.findById("test@test.com").orElseThrow();
        Category findCategory = categoryRepository.findById(savedCategory.getId()).orElseThrow();


        CategoryUser categoryUser = new CategoryUser();
        categoryUser.setCategory(findCategory);
        categoryUser.setMember(findMember2);

        CategoryUser categoryUser2 = new CategoryUser();
        categoryUser2.setCategory(findCategory);
        categoryUser2.setMember(findMember3);

        categoryUserRepository.save(categoryUser);
        categoryUserRepository.save(categoryUser2);
    }

    @Test
    public void 마커추가() {
        Member findMember = memberRepository.findById("test@test.com").orElseThrow();
        Category category = categoryRepository.findById(1L).orElseThrow();

        Marker marker = new Marker();
        marker.setLat(new BigDecimal("37.12345678"));
        marker.setLng(new BigDecimal("127.12345678"));
        marker.setDescription("맘모스빵이 개 맛도리임");
        marker.setTitle("맘모스빵맛집임");
        marker.setMemberId(findMember);
        marker.setCategoryId(category);

        // 해당유저가 카테고리에 속해있는지 검증
        List<Member> memberList = categoryUserRepository.findMembersByCategoryId(category.getId());

        System.out.println(memberList);

        if(memberList.contains(findMember)) {
            System.out.println("카테고리에 속한 유저");
            markerRepository.save(marker);
        } else {
            System.out.println("없는유저");
        }
    }


}
