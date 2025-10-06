// src/main/java/com/tailieuptit/demo/controller/CategoryController.java
package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.Category;
import com.tailieuptit.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    // API endpoint - Search categories
    @GetMapping("/api/search")
    @ResponseBody
    public List<Category> searchCategories(@RequestParam("q") String query) {
        try {
            return categoryService.searchCategories(query);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // API endpoint - Create new category
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<?> createCategory(@RequestParam("name") String name,
                                            @RequestParam("description") String description,
                                            HttpSession session) {
        try {
            if (!userService.isLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Vui lòng đăng nhập"));
            }

            Category category = categoryService.createCategory(name, description);
            return ResponseEntity.ok(category);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get all categories - API endpoint
    @GetMapping("/api/all")
    @ResponseBody
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }
}