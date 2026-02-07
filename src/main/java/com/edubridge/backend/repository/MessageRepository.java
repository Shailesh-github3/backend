package com.edubridge.backend.repository;

import com.edubridge.backend.model.Chat;
import com.edubridge.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Find all messages in a specific chat room, ordered by time
    List<Message> findByChatIdOrderBySentAtAsc(Long chatId);
}