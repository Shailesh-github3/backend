package com.edubridge.backend.service;

import com.edubridge.backend.exception.ResourceNotFoundException;
import com.edubridge.backend.model.Chat;
import com.edubridge.backend.model.Message;
import com.edubridge.backend.model.Role;
import com.edubridge.backend.model.User;
import com.edubridge.backend.repository.ChatRepository;
import com.edubridge.backend.repository.MessageRepository;
import com.edubridge.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        // 1. Load Users
        User sender = userRepository.findById(senderId).orElseThrow(() -> new ResourceNotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        // 2. Identify who is School and who is College
        User schoolStudent;
        User collegeStudent;

        if (Role.SCHOOL_STUDENT.equals(sender.getRole())) {
            schoolStudent = sender;
            collegeStudent = receiver;
        } else if (Role.SCHOOL_STUDENT.equals(receiver.getRole())) {
            schoolStudent = receiver;
            collegeStudent = sender;
        } else {
            throw new RuntimeException("Chat must involve a school and college student");
        }

        // 3. Find or Create Chat Room (Explicit logic to avoid Lambda errors)
        Chat chat;

        // Try to find the chat one way
        Optional<Chat> chatOpt = chatRepository.findBySchoolStudentIdAndCollegeStudentId(schoolStudent.getId(), collegeStudent.getId());

        if (chatOpt.isPresent()) {
            chat = chatOpt.get();
        } else {
            // Create new chat room
            chat = new Chat();
            chat.setSchoolStudent(schoolStudent);
            chat.setCollegeStudent(collegeStudent);
            chat.setCreatedAt(new Date());
            chat = chatRepository.save(chat);
        }

        // 4. Create Message
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setSentAt(new Date());
        message.setRead(false);

        messageRepository.save(message);

        // 5. Update Chat Timestamp
        chat.setLastMessageAt(new Date());
        chatRepository.save(chat);

        return message;
    }

    public List<Message> getChatHistory(Long userId1, Long userId2) {
        User u1 = userRepository.findById(userId1).orElseThrow(() -> new RuntimeException("User 1 not found"));
        User u2 = userRepository.findById(userId2).orElseThrow(() -> new RuntimeException("User 2 not found"));

        // Try finding the chat room
        Optional<Chat> chatOpt = chatRepository.findBySchoolStudentIdAndCollegeStudentId(u1.getId(), u2.getId());

        if (!chatOpt.isPresent()) {
            chatOpt = chatRepository.findBySchoolStudentIdAndCollegeStudentId(u2.getId(), u1.getId());
        }

        if (chatOpt.isEmpty()) {
            return List.of(); // Return empty list if no chat exists
        }

        Chat chat = chatOpt.get();
        return messageRepository.findByChatIdOrderBySentAtAsc(chat.getId());
    }
}