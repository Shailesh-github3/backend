package com.edubridge.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "forum_post")
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Forum ID is required")
    @Column(nullable = false)
    private Long forumId;

    @NotNull(message = "Author ID is required")
    @Column(nullable = false)
    private Long authorId;

    @NotBlank(message = "Post title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Post content cannot be empty")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_deleted")
    private boolean deleted = false;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;
}