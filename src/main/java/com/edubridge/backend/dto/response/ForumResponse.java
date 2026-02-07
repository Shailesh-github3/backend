package com.edubridge.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForumResponse {
    private Long id;
    private Long collegeId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private Long postCount;  // Number of posts in this forum

    public static ForumResponse fromForum(com.edubridge.backend.model.Forum forum, Long postCount) {
        ForumResponse response = new ForumResponse();
        response.setId(forum.getId());
        response.setCollegeId(forum.getCollegeId());
        response.setTitle(forum.getTitle());
        response.setDescription(forum.getDescription());
        response.setCreatedAt(forum.getCreatedAt().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
        response.setPostCount(postCount);
        return response;
    }
}