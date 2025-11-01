// src/main/java/com/tailieuptit/demo/service/DocumentService.java
package com.tailieuptit.demo.service;

import com.tailieuptit.demo.entity.Category;
import com.tailieuptit.demo.entity.Comment;
import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.repository.CategoryRepository;
import com.tailieuptit.demo.repository.CommentRepository;
import com.tailieuptit.demo.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final String UPLOAD_DIR = "uploads/";

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public List<Document> getDocumentsByCategory(Integer categoryId) {
        return documentRepository.findByCategory_CategoryId(categoryId);
    }

    public List<Document> getDocumentsByUser(Integer userId) {
        return documentRepository.findByUser_UserId(userId);
    }

    public List<Document> searchDocuments(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllDocuments();
        }
        return documentRepository.searchDocuments(query);
    }

    public Optional<Document> getDocumentById(Integer id) {
        return documentRepository.findById(id);
    }

    private Category resolveCategory(Integer categoryId, String categoryName, String description) {
        if (categoryId != null && categoryId > 0) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
        } else if (categoryName != null && !categoryName.trim().isEmpty()) {
            Optional<Category> existing = categoryRepository.findByName(categoryName.trim());
            if (existing.isPresent()) {
                return existing.get();
            } else {
                Category newCategory = new Category();
                newCategory.setName(categoryName.trim());
                newCategory.setDescription(description != null ? description.trim() : "");
                return categoryRepository.save(newCategory);
            }
        } else {
            throw new RuntimeException("Vui lòng chọn hoặc tạo danh mục!");
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        return filename;
    }

    private void deletePhysicalFile(String filePath) {
        try {
            if (filePath != null && filePath.startsWith("/uploads/")) {
                String actualFilePath = filePath.substring("/uploads/".length());
                Path file = Paths.get(UPLOAD_DIR).resolve(actualFilePath);
                if (Files.exists(file)) {
                    Files.delete(file);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not delete physical file: " + e.getMessage());
        }
    }

    public Document createDocument(Document document, MultipartFile file,
                                   Integer categoryId, String categoryName,
                                   String newCategoryDescription, User user) throws IOException {

        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn file để upload!");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("File quá lớn! Tối đa 10MB.");
        }

        // Handle category
        Category category = resolveCategory(categoryId, categoryName, newCategoryDescription);

        // Save file
        String fileName = saveFile(file);

        // Set document properties
        document.setUser(user);
        document.setCategory(category);
        document.setFilePath("/uploads/" + fileName);

        return documentRepository.save(document);
    }

    public String deleteDocument(Integer id) {
        Document doc = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));

        String title = doc.getTitle();

        // Delete associated comments
        List<Comment> comments = commentRepository.findByDocument_DocumentId(id);
        if (!comments.isEmpty()) {
            commentRepository.deleteAll(comments);
        }

        // Delete physical file
        deletePhysicalFile(doc.getFilePath());

        // Delete from database
        documentRepository.delete(doc);

        return title;
    }

    public ResponseEntity<?> downloadDocument(Integer id) {
        try {
            Document doc = documentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));

            String filePath = doc.getFilePath();
            if (filePath.startsWith("/uploads/")) {
                filePath = filePath.substring("/uploads/".length());
            }

            Path file = Paths.get(UPLOAD_DIR).resolve(filePath);

            if (!Files.exists(file)) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + doc.getTitle() + ".txt\"")
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Demo content for: " + doc.getTitle());
            }

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                String originalFilename = doc.getTitle();
                String fileExtension = "";
                if (filePath.contains(".")) {
                    fileExtension = filePath.substring(filePath.lastIndexOf("."));
                    if (!originalFilename.endsWith(fileExtension)) {
                        originalFilename += fileExtension;
                    }
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + originalFilename + "\"")
                        .body(resource);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error downloading file: " + e.getMessage());
        }
    }
}