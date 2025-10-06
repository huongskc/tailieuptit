// src/main/java/com/tailieuptit/demo/service/CategoryService.java
package com.tailieuptit.demo.service;

import com.tailieuptit.demo.entity.Category;
import com.tailieuptit.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> searchCategories(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCategories();
        }

        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .filter(cat -> cat.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Category createCategory(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Tên danh mục không được để trống");
        }

        // Check if category already exists
        Optional<Category> existing = categoryRepository.findByName(name.trim());
        if (existing.isPresent()) {
            return existing.get();
        }

        Category newCategory = new Category();
        newCategory.setName(name.trim());
        newCategory.setDescription(description != null ? description.trim() : "");

        return categoryRepository.save(newCategory);
    }

    public Optional<Category> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}