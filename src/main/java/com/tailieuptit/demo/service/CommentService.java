// src/main/java/com/tailieuptit/demo/service/CommentService.java
package com.tailieuptit.demo.service;

import com.tailieuptit.demo.entity.Comment;
import com.tailieuptit.demo.entity.Document;
import com.tailieuptit.demo.entity.User;
import com.tailieuptit.demo.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public List<Comment> getCommentsByDocument(Integer documentId) {
        return commentRepository.findByDocument_DocumentId(documentId);
    }

    public List<Comment> getCommentsByUser(Integer userId) {
        return commentRepository.findByUser_UserId(userId);
    }

    public Comment createComment(String content, Document document, User user) {
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Nội dung bình luận không được để trống");
        }

        Comment comment = new Comment();
        comment.setContent(content.trim());
        comment.setDocument(document);
        comment.setUser(user);

        return commentRepository.save(comment);
    }

    public Integer deleteComment(Integer commentId, User currentUser) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            throw new RuntimeException("Không tìm thấy bình luận");
        }

        Comment comment = commentOpt.get();

        // Check permission - only comment owner or admin can delete
        boolean isOwner = comment.getUser().getUserId().equals(currentUser.getUserId());
        boolean isAdmin = "admin".equals(currentUser.getUsername());

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền xóa bình luận này");
        }

        Integer documentId = comment.getDocument().getDocumentId();
        commentRepository.delete(comment);

        return documentId;
    }

    public Optional<Comment> getCommentById(Integer id) {
        return commentRepository.findById(id);
    }
}