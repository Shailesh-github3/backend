package com.edubridge.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ForumCommentResponse {
    private Long id;
    private Long postId;
    private Long authorId;
    private String authorName;
    private String authorEmail;
    private String content;
    private LocalDateTime createdAt;

    public static ForumCommentResponse fromComment(com.edubridge.backend.model.ForumComment comment,
                                                   String authorName, String authorEmail) {
        ForumCommentResponse response = new ForumCommentResponse();
        response.setId(comment.getId());
        response.setPostId(comment.getPostId());
        response.setAuthorId(comment.getAuthorId());
        response.setAuthorName(authorName);
        response.setAuthorEmail(authorEmail);
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
        return response;
    }
}