package com.tailieuptit.demo.repository;

import com.tailieuptit.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByDocument_DocumentId(Integer documentId);
    List<Comment> findByUser_UserId(Integer userId);
}
