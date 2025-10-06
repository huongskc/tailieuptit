// src/main/java/com/tailieuptit/demo/controller/CommentController.java
package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.Comment;
import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.service.*;
import com.tailieuptit.demo.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    // Thêm comment vào document
    @PostMapping("/document/{documentId}/add")
    public String addComment(@PathVariable Integer documentId,
                             @ModelAttribute Comment newComment,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            if (!userService.isLoggedIn(session)) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để bình luận!");
                return "redirect:/auth/login";
            }

            User currentUser = userService.getCurrentUser(session)
                    .orElseThrow(() -> new RuntimeException("User session expired"));

            Document doc = documentService.getDocumentById(documentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));

            commentService.createComment(newComment.getContent(), doc, currentUser);

            redirectAttributes.addFlashAttribute("message", "Đã thêm bình luận!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm bình luận: " + e.getMessage());
        }

        return "redirect:/document/" + documentId;
    }

    // Xóa comment - chỉ admin hoặc chủ sở hữu
    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Integer commentId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        try {
            if (!userService.isLoggedIn(session)) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập!");
                return "redirect:/auth/login";
            }

            User currentUser = userService.getCurrentUser(session)
                    .orElseThrow(() -> new RuntimeException("User session expired"));

            Integer documentId = commentService.deleteComment(commentId, currentUser);

            redirectAttributes.addFlashAttribute("message", "Đã xóa bình luận!");
            return "redirect:/document/" + documentId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa bình luận: " + e.getMessage());
            return "redirect:/";
        }
    }
}