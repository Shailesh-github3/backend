package com.edubridge.backend.dto.response;

import com.edubridge.backend.model.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private String profileImage;

    // Static factory method to convert User entity to DTO
    public static UserResponse fromUser(com.edubridge.backend.model.User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setActive(user.isActive());
        response.setCreatedAt(user.getCreatedAt().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
        response.setProfileImage(user.getProfileImage());
        return response;
    }
}