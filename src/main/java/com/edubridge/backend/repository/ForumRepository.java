package com.edubridge.backend.repository;

import com.edubridge.backend.model.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {

    // Find forums by college ID
    List<Forum> findByCollegeId(Long collegeId);

    // Count posts in a forum
    @Query("SELECT COUNT(fp) FROM ForumPost fp WHERE fp.forumId = :forumId")
    Long countPostsByForumId(@Param("forumId") Long forumId);
}