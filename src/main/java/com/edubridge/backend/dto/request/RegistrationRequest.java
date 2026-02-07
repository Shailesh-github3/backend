package com.edubridge.backend.dto.request;

import com.edubridge.backend.model.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationRequest {

    // ===== USER FIELDS =====

    @Email(message = "Email should be a valid email address")
    @NotBlank(message = "Email is mandatory")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotNull(message = "Role is mandatory")
    private Role role;

    // ===== SCHOOL STUDENT FIELDS =====

    private String name;

    private String schoolName;

    private String classLevel;

    private String interests;

    // ===== COLLEGE STUDENT FIELDS =====

    private Long collegeId;

    private String branch;

    private String yearOfStudy;

    private String bio;

    private String skills;

    private String availabilityStatus;
}