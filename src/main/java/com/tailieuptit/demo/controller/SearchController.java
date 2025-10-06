// src/main/java/com/tailieuptit/demo/controller/SearchController.java
package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.Category;
import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.service.CategoryService;
import com.tailieuptit.demo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String searchDocuments(@RequestParam("q") String query, Model model) {
        List<Category> categories = categoryService.getAllCategories();
        List<Document> documents = documentService.searchDocuments(query);

        model.addAttribute("categories", categories);
        model.addAttribute("documents", documents);
        model.addAttribute("searchQuery", query);
        model.addAttribute("title", "Tìm kiếm: " + query);

        return "index";
    }
}