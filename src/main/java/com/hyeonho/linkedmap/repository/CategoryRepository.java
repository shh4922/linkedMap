package com.hyeonho.linkedmap.repository;

import com.hyeonho.linkedmap.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
