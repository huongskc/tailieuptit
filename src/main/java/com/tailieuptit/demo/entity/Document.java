package com.tailieuptit.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Integer documentId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Người upload

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;  // Thuộc loại tài liệu nào

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "file_path", nullable = false, length = 255)
    private String filePath;

    public Document() {}

    public Document(Integer documentId, User user, Category category,
                    String title, String description, String filePath) {
        this.documentId = documentId;
        this.user = user;
        this.category = category;
        this.title = title;
        this.description = description;
        this.filePath = filePath;
    }

    public Integer getDocumentId() { return documentId; }
    public void setDocumentId(Integer documentId) { this.documentId = documentId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    @Override
    public String toString() {
        return "Document{" +
                "documentId=" + documentId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
