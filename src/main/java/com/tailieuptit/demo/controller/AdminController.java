// src/main/java/com/tailieuptit/demo/controller/AdminController.java
package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.Category;
import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.service.*;
import com.tailieuptit.demo.service.DocumentService;
// THAY ĐỔI: Xóa bỏ inject Repository
// import com.tailieuptit.demo.repository.UserRepository;
// import com.tailieuptit.demo.repository.DocumentRepository;
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

    // THAY ĐỔI: Đã xóa bỏ UserRepository và DocumentRepository

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
        // THAY ĐỔI: Gọi sang UserService
        List<User> users = userService.getAllUsers();

        model.addAttribute("totalDocuments", documents.size());
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("title", "Admin Dashboard");

        return "admin/dashboard";
    }

    // Debug endpoint - kiểm tra users trong database
    @GetMapping("/debug/users")
    @ResponseBody
    public String debugUsers(HttpSession session) {
        if (!userService.isAdmin(session)) {
            return "Access denied - Admin only";
        }

        try {
            // THAY ĐỔI: Gọi sang UserService
            List<User> users = userService.getAllUsers();
            StringBuilder result = new StringBuilder("Users in database:\n");
            for (User user : users) {
                result.append("ID: ").append(user.getUserId())
                        .append(", Username: ").append(user.getUsername())
                        .append(", Email: ").append(user.getEmail())
                        .append(", Password: ").append(user.getPassword())
                        .append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Debug endpoint - kiểm tra documents
    @GetMapping("/debug/documents")
    @ResponseBody
    public String debugDocuments(HttpSession session) {
        if (!userService.isAdmin(session)) {
            return "Access denied - Admin only";
        }

        try {
            // THAY ĐỔI: Gọi sang DocumentService
            List<Document> documents = documentService.getAllDocuments();
            StringBuilder result = new StringBuilder("Documents in database:\n");
            for (Document doc : documents) {
                result.append("ID: ").append(doc.getDocumentId())
                        .append(", Title: ").append(doc.getTitle())
                        .append(", FilePath: ").append(doc.getFilePath())
                        .append(", User: ").append(doc.getUser() != null ? doc.getUser().getUsername() : "null")
                        .append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}