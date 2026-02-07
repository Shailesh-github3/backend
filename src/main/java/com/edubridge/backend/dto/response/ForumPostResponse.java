package com.edubridge.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForumPostResponse {
    private Long id;
    private Long forumId;
    private Long authorId;
    private String authorName;
    private String authorEmail;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long commentCount;

    public static ForumPostResponse fromPost(com.edubridge.backend.model.ForumPost post,
                                             String authorName, String authorEmail, Long commentCount) {
        ForumPostResponse response = new ForumPostResponse();
        response.setId(post.getId());
        response.setForumId(post.getForumId());
        response.setAuthorId(post.getAuthorId());
        response.setAuthorName(authorName);
        response.setAuthorEmail(authorEmail);
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setCreatedAt(post.getCreatedAt().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
        response.setUpdatedAt(post.getUpdatedAt().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
        response.setCommentCount(commentCount);
        return response;
    }
}