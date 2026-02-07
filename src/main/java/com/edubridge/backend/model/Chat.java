package com.edubridge.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "School Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_student_id", nullable = false)
    private User schoolStudent;

    @NotNull(message = "College Student is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_student_id", nullable = false)
    private User collegeStudent;

    @CreationTimestamp
    private Date createdAt;

    private Date lastMessageAt;
}