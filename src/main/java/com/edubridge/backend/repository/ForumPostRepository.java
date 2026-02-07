package com.edubridge.backend.repository;

import com.edubridge.backend.model.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    // Find posts by forum ID
    List<ForumPost> findByForumId(Long forumId);

    // Find posts by author ID
    List<ForumPost> findByAuthorId(Long authorId);

    // Count comments on a post
    @Query("SELECT COUNT(fc) FROM ForumComment fc WHERE fc.postId = :postId")
    Long countCommentsByPostId(@Param("postId") Long postId);
}