package com.edubridge.backend.repository;

import com.edubridge.backend.model.ForumComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {

    // Find comments by post ID
    List<ForumComment> findByPostId(Long postId);

    // Find comments by author ID
    List<ForumComment> findByAuthorId(Long authorId);
}