package com.edubridge.backend.dto.request;

import lombok.Data;

@Data
public class ProfileUpdateRequest {

    // ===== COMMON FIELD =====
    private String name;  // Optional update

    // ===== SCHOOL STUDENT FIELDS =====
    private String schoolName;
    private String classLevel;
    private String interests;

    // ===== COLLEGE STUDENT FIELDS =====
    private String bio;
    private String skills;
    private String availabilityStatus;


}