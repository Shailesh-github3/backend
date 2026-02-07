package com.edubridge.backend.controller;

import com.edubridge.backend.dto.request.SendMessageRequest;
import com.edubridge.backend.dto.response.MessageResponse;
import com.edubridge.backend.model.Message;
import com.edubridge.backend.service.ChatService;
import com.edubridge.backend.service.UserService;
import com.edubridge.backend.model.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@Valid @RequestBody SendMessageRequest request,
                                         @AuthenticationPrincipal UserDetails currentUser) {
        // ✅ No try-catch needed
        User sender = userService.getUserByEmail(currentUser.getUsername());
        Message message = chatService.sendMessage(
                sender.getId(),
                request.getReceiverId(),
                request.getContent()
        );
        return ResponseEntity.ok(MessageResponse.fromMessage(message));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChatHistory(@RequestParam Long userId,
                                            @AuthenticationPrincipal UserDetails currentUser) {
        // ✅ No try-catch needed
        User sender = userService.getUserByEmail(currentUser.getUsername());
        List<Message> history = chatService.getChatHistory(sender.getId(), userId);

        List<MessageResponse> response = history.stream()
                .map(MessageResponse::fromMessage)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}