package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Integer> {
    // Lấy danh sách document theo categoryId
    List<Document> findByCategory_CategoryId(Integer categoryId);

    // Lấy danh sách document theo userId
    List<Document> findByUser_UserId(Integer userId);

    // Tìm kiếm nâng cao - sử dụng @Query
    @Query("SELECT d FROM Document d WHERE " +
            "LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(d.category.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(d.user.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Document> searchDocuments(@Param("query") String query);
}