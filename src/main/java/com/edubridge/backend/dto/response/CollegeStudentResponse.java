package com.edubridge.backend.dto.response;

import lombok.Data;

@Data
public class CollegeStudentResponse {
    private Long id;
    private Long userId;
    private String name;
    private Long collegeId;
    private String branch;
    private String yearOfStudy;
    private String bio;
    private String skills;
    private String availabilityStatus;
    private String email;  // From User entity

    public static CollegeStudentResponse fromProfile(
            com.edubridge.backend.model.CollegeStudentProfile profile) {
        CollegeStudentResponse response = new CollegeStudentResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setName(profile.getName());
        response.setCollegeId(profile.getCollegeId());
        response.setBranch(profile.getBranch());
        response.setYearOfStudy(profile.getYearOfStudy());
        response.setBio(profile.getBio());
        response.setSkills(profile.getSkills());
        response.setAvailabilityStatus(profile.getAvailabilityStatus());
        response.setEmail(profile.getUser().getEmail());
        return response;
    }
}