package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Optional<Category> findByName(String name);
    List<Category> findByNameContainingIgnoreCase(String query);
    Optional<Category> findByNameIgnoreCase(String name);
}