package com.edubridge.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "college_student_profile")
public class CollegeStudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User link is mandatory")
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull(message = "College ID is mandatory")
    @Column(nullable = false)
    private Long collegeId;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Branch is mandatory")
    @Column(nullable = false)
    private String branch;

    private String yearOfStudy;
    private String bio;
    private String skills;
    private String availabilityStatus;

    @CreationTimestamp
    private Date createdAt;
}