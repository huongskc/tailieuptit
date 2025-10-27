// src/main/java/com/tailieuptit/demo/controller/AdminController.java
package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.Category;
import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.service.*;
import com.tailieuptit.demo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CategoryService categoryService;

    // Check admin access for all admin routes
    private boolean checkAdminAccess(HttpSession session, RedirectAttributes redirectAttributes) {
        if (!userService.isLoggedIn(session)) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập!");
            return false;
        }

        if (!userService.isAdmin(session)) {
            redirectAttributes.addFlashAttribute("error", "Chỉ admin mới có quyền truy cập!");
            return false;
        }

        return true;
    }

    // Admin dashboard
    @GetMapping
    public String adminDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdminAccess(session, redirectAttributes)) {
            return "redirect:/";
        }

        List<Document> documents = documentService.getAllDocuments();
        List<Category> categories = categoryService.getAllCategories();
        List<User> users = userService.getAllUsers();

        model.addAttribute("totalDocuments", documents.size());
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("title", "Admin Dashboard");

        return "admin/dashboard";
    }
}