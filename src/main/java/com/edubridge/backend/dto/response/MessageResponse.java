package com.edubridge.backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String senderEmail;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;

    public static MessageResponse fromMessage(com.edubridge.backend.model.Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setChatId(message.getChat().getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderEmail(message.getSender().getEmail());
        response.setContent(message.getContent());
        response.setSentAt(message.getSentAt().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime());
        response.setRead(message.isRead());
        return response;
    }
}