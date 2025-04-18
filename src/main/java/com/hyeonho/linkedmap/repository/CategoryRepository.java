package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category findByIdAndDeletedAtIsNull(Long categoryId);
}
