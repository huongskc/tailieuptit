// src/main/java/com/tailieuptit/demo/controller/ProfileController.java
package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.Comment;
import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.service.*;
import com.tailieuptit.demo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public String userProfile(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!userService.isLoggedIn(session)) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để xem profile!");
                return "redirect:/auth/login";
            }

            User user = userService.getCurrentUser(session)
                    .orElseThrow(() -> new RuntimeException("User session expired"));

            // Get user's documents and comments
            List<Document> userDocuments = documentService.getDocumentsByUser(user.getUserId());
            List<Comment> userComments = commentService.getCommentsByUser(user.getUserId());

            model.addAttribute("user", user);
            model.addAttribute("userDocuments", userDocuments);
            model.addAttribute("userComments", userComments);
            model.addAttribute("title", "Thông tin cá nhân - " + user.getUsername());

            return "profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/";
        }
    }
}