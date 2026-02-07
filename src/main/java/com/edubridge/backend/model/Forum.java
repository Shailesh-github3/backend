package com.edubridge.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "forum")
public class Forum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "College ID is required")
    @Column(nullable = false)
    private Long collegeId;

    @NotBlank(message = "Forum title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Forum description is required")
    @Column(nullable = false)
    private String description;

    @CreationTimestamp
    private Date createdAt;
}