// src/main/java/com/tailieuptit/demo/controller/HomeController.java (Refactored - Much cleaner!)
package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.Category;
import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.service.CategoryService;
import com.tailieuptit.demo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam; // THÊM IMPORT
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CategoryService categoryService;

    // Trang chủ - chỉ hiển thị danh mục và tài liệu
    @GetMapping("/")
    public String home(Model model) {

        List<Category> categories = categoryService.getAllCategories();
        List<Document> documents = documentService.getAllDocuments();

        model.addAttribute("categories", categories);
        model.addAttribute("documents", documents);
        model.addAttribute("title", "Trang chủ - Tài Liệu PTIT");

        return "index";
    }

    // Trang giới thiệu
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Giới thiệu");
        return "about";
    }
}

