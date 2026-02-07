package com.edubridge.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "forum_comment")
public class ForumComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Post ID is required")
    @Column(nullable = false)
    private Long postId;

    @NotNull(message = "Author ID is required")
    @Column(nullable = false)
    private Long authorId;

    @NotBlank(message = "Comment content cannot be empty")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    private Date createdAt;
}