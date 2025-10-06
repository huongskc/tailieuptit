package com.tailieuptit.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;   // Người viết bình luận

    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;  // Tài liệu được bình luận

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // ===== Constructors =====
    public Comment() {}

    public Comment(Integer commentId, User user, Document document, String content) {
        this.commentId = commentId;
        this.user = user;
        this.document = document;
        this.content = content;
    }

    // ===== Getter & Setter =====
    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", content='" + content + '\'' +
                '}';
    }
}
