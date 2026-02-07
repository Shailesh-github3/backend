package com.edubridge.backend.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ChatHistoryResponse {
    private Long chatId;
    private Long otherUserId;
    private String otherUserEmail;
    private List<MessageResponse> messages;

    public ChatHistoryResponse(Long chatId, Long otherUserId, String otherUserEmail,
                               List<MessageResponse> messages) {
        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.otherUserEmail = otherUserEmail;
        this.messages = messages;
    }
}