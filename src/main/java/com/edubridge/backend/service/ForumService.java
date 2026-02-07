package com.edubridge.backend.service;

import com.edubridge.backend.dto.request.CreateCommentRequest;
import com.edubridge.backend.dto.request.CreateForumRequest;
import com.edubridge.backend.dto.request.CreatePostRequest;
import com.edubridge.backend.exception.BadRequestException;
import com.edubridge.backend.exception.ResourceNotFoundException;
import com.edubridge.backend.model.*;
import com.edubridge.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ForumService {

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private ForumCommentRepository forumCommentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollegeProfileRepository collegeProfileRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    // ✅ Only college students can create forums
    @Transactional
    public Forum createForum(CreateForumRequest request, Long userId) {
        // Verify user is a college student
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.COLLEGE_STUDENT) {
            throw new BadRequestException("Only college students can create forums");
        }

        // ✅ Verify college exists
        boolean collegeExists = collegeRepository.existsById(request.getCollegeId());
        if (!collegeExists) {
            throw new BadRequestException("College with ID " + request.getCollegeId() + " does not exist. Please create the college first.");
        }

        Forum forum = new Forum();
        forum.setCollegeId(request.getCollegeId());
        forum.setTitle(request.getTitle());
        forum.setDescription(request.getDescription());
        forum.setCreatedAt(new Date());

        return forumRepository.save(forum);
    }

    // ✅ Only college students can create posts
    @Transactional
    public ForumPost createPost(CreatePostRequest request, Long userId) {
        // Verify user is a college student
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.COLLEGE_STUDENT) {
            throw new BadRequestException("Only college students can create posts");
        }

        // Verify forum exists
        boolean forumExists = forumRepository.findById(request.getForumId()).isPresent();
        if (!forumExists) {
            throw new ResourceNotFoundException("Forum not found");
        }

        ForumPost post = new ForumPost();
        post.setForumId(request.getForumId());
        post.setAuthorId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());

        return forumPostRepository.save(post);
    }

    // ✅ Only college students can comment
    @Transactional
    public ForumComment createComment(CreateCommentRequest request, Long userId) {
        // Verify user is a college student
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.COLLEGE_STUDENT) {
            throw new BadRequestException("Only college students can comment on posts. Please send a private message instead.");
        }

        // Verify post exists
        boolean postExists = forumPostRepository.findById(request.getPostId()).isPresent();
        if (!postExists) {
            throw new ResourceNotFoundException("Post not found");
        }

        ForumComment comment = new ForumComment();
        comment.setPostId(request.getPostId());
        comment.setAuthorId(userId);
        comment.setContent(request.getContent());
        comment.setCreatedAt(new Date());

        return forumCommentRepository.save(comment);
    }

    // ✅ Anyone can view forums (read-only)
    public List<Forum> getAllForums() {
        return forumRepository.findAll();
    }

    // ✅ Anyone can view forums by college
    public List<Forum> getForumsByCollege(Long collegeId) {
        return forumRepository.findByCollegeId(collegeId);
    }

    // ✅ Anyone can view posts in a forum
    public List<ForumPost> getPostsByForum(Long forumId) {
        return forumPostRepository.findByForumId(forumId);
    }

    // ✅ Anyone can view comments on a post
    public List<ForumComment> getCommentsByPost(Long postId) {
        return forumCommentRepository.findByPostId(postId);
    }
}