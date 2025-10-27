// src/main/java/com/tailieuptit/demo/controller/DocumentController.java
package com.tailieuptit.demo.controller;

import com.tailieuptit.demo.entity.Category;
import com.tailieuptit.demo.entity.Comment;
import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.service.*;
// THAY ĐỔI: Xóa bỏ inject Repository
// import com.tailieuptit.demo.repository.CategoryRepository;
// import com.tailieuptit.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private UserService userService;

    // THAY ĐỔI: Inject Service thay vì Repository
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentService commentService;

    // Trang upload
    @GetMapping("/upload")
    public String uploadPage(Model model) {
        // THAY ĐỔI: Gọi CategoryService
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("categories", categories);
        model.addAttribute("document", new Document());
        model.addAttribute("title", "Upload Tài Liệu");
        return "upload";
    }

    // Xử lý upload document
    @PostMapping("/add")
    public String addDocument(@ModelAttribute Document document,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam(value = "category.categoryId", required = false) Integer categoryId,
                              @RequestParam(value = "categoryName", required = false) String categoryName,
                              @RequestParam(value = "newCategoryDescription", required = false) String newCategoryDescription,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        try {
            // Check authentication
            if (!userService.isLoggedIn(session)) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để upload tài liệu!");
                return "redirect:/auth/login";
            }

            User currentUser = userService.getCurrentUser(session)
                    .orElseThrow(() -> new RuntimeException("User session expired"));

            // Logic này đã nằm trong DocumentService là đúng
            Document savedDocument = documentService.createDocument(
                    document, file, categoryId, categoryName, newCategoryDescription, currentUser
            );

            redirectAttributes.addFlashAttribute("message",
                    "Upload tài liệu thành công vào danh mục: " + savedDocument.getCategory().getName());
            return "redirect:/profile";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/document/upload";
        }
    }

    // Chi tiết document
    @GetMapping("/{id}")
    public String documentDetail(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Optional<Document> docOpt = documentService.getDocumentById(id);
            if (!docOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài liệu!");
                return "redirect:/";
            }

            Document doc = docOpt.get();
            // THAY ĐỔI: Gọi CommentService
            List<Comment> comments = commentService.getCommentsByDocument(id);

            model.addAttribute("doc", doc);
            model.addAttribute("comments", comments);
            model.addAttribute("newComment", new Comment());
            model.addAttribute("title", doc.getTitle() + " - Tài Liệu PTIT");

            return "document-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể tải chi tiết tài liệu: " + e.getMessage());
            return "redirect:/";
        }
    }

    // Download file
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadDocument(@PathVariable Integer id) {
        try {
            // Logic này đã nằm trong DocumentService là đúng
            return documentService.downloadDocument(id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error downloading file: " + e.getMessage());
        }
    }

    // Delete document - Admin only
    @PostMapping("/{id}/delete")
    public String deleteDocument(@PathVariable Integer id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        try {
            // Check authentication and admin permission
            if (!userService.isLoggedIn(session)) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập!");
                return "redirect:/auth/login";
            }

            if (!userService.isAdmin(session)) {
                redirectAttributes.addFlashAttribute("error", "Chỉ admin mới có quyền xóa tài liệu!");
                return "redirect:/document/" + id;
            }

            // Logic này đã nằm trong DocumentService là đúng
            String documentTitle = documentService.deleteDocument(id);
            redirectAttributes.addFlashAttribute("message",
                    "Đã xóa tài liệu: " + documentTitle + " thành công!");
            return "redirect:/";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa tài liệu: " + e.getMessage());
            return "redirect:/document/" + id;
        }
    }

    // Lọc theo category
    @GetMapping("/category/{id}")
    public String documentsByCategory(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // THAY ĐỔI: Gọi CategoryService
            Optional<Category> categoryOpt = categoryService.getCategoryById(id);
            if (!categoryOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy danh mục!");
                return "redirect:/";
            }

            Category category = categoryOpt.get();
            List<Document> documents = documentService.getDocumentsByCategory(id);
            // THAY ĐỔI: Gọi CategoryService
            List<Category> categories = categoryService.getAllCategories();

            model.addAttribute("categories", categories);
            model.addAttribute("documents", documents);
            model.addAttribute("selectedCategory", category);
            model.addAttribute("title", category.getName() + " - Tài Liệu PTIT");

            return "index";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể tải tài liệu theo danh mục: " + e.getMessage());
            return "redirect:/";
        }
    }

    // Trang tất cả tài liệu
    @GetMapping("/all")
    public String allDocuments(Model model) {
        // THAY ĐỔI: Gọi CategoryService
        List<Category> categories = categoryService.getAllCategories();
        List<Document> documents = documentService.getAllDocuments();

        model.addAttribute("categories", categories);
        model.addAttribute("documents", documents);
        model.addAttribute("title", "Tất cả tài liệu");

        return "index";
    }
}