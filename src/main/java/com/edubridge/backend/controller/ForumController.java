package com.edubridge.backend.controller;

import com.edubridge.backend.dto.request.CreateCommentRequest;
import com.edubridge.backend.dto.request.CreateForumRequest;
import com.edubridge.backend.dto.request.CreatePostRequest;
import com.edubridge.backend.dto.response.ForumCommentResponse;
import com.edubridge.backend.dto.response.ForumPostResponse;
import com.edubridge.backend.dto.response.ForumResponse;
import com.edubridge.backend.model.*;
import com.edubridge.backend.service.ForumService;
import com.edubridge.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumService forumService;

    @Autowired
    private UserService userService;

    // Create a new forum (College students only)
    @PostMapping("/create")
    public ResponseEntity<?> createForum(@Valid @RequestBody CreateForumRequest request,
                                         @AuthenticationPrincipal UserDetails currentUser) {
        var user = userService.getUserByEmail(currentUser.getUsername());
        Forum forum = forumService.createForum(request, user.getId());
        return ResponseEntity.ok(forum);
    }

    // Create a new post in a forum (College students only)
    @PostMapping("/post")
    public ResponseEntity<?> createPost(@Valid @RequestBody CreatePostRequest request,
                                        @AuthenticationPrincipal UserDetails currentUser) {
        var user = userService.getUserByEmail(currentUser.getUsername());
        ForumPost post = forumService.createPost(request, user.getId());
        return ResponseEntity.ok(post);
    }

    // Add a comment to a post (College students only)
    @PostMapping("/comment")
    public ResponseEntity<?> createComment(@Valid @RequestBody CreateCommentRequest request,
                                           @AuthenticationPrincipal UserDetails currentUser) {
        var user = userService.getUserByEmail(currentUser.getUsername());
        ForumComment comment = forumService.createComment(request, user.getId());
        return ResponseEntity.ok(comment);
    }

    // Get all forums (Anyone can view)
    @GetMapping("/all")
    public ResponseEntity<?> getAllForums() {
        List<Forum> forums = forumService.getAllForums();

        // Enrich with post counts
        List<ForumResponse> response = forums.stream()
                .map(forum -> {
                    Long postCount = (long)forumService.getPostsByForum(forum.getId()).size();
                    return ForumResponse.fromForum(forum, postCount);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Get forums by college (Anyone can view)
    @GetMapping("/college/{collegeId}")
    public ResponseEntity<?> getForumsByCollege(@PathVariable Long collegeId) {
        List<Forum> forums = forumService.getForumsByCollege(collegeId);

        List<ForumResponse> response = forums.stream()
                .map(forum -> {
                    Long postCount = (long)forumService.getPostsByForum(forum.getId()).size();
                    return ForumResponse.fromForum(forum, postCount);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Get all posts in a forum (Anyone can view)
    @GetMapping("/{forumId}/posts")
    public ResponseEntity<?> getPostsByForum(@PathVariable Long forumId) {
        List<ForumPost> posts = forumService.getPostsByForum(forumId);

        List<ForumPostResponse> response = posts.stream()
                .map(post -> {
                    // CORRECT: Get author by ID (not email!)
                    User authorUser = userService.getUserById(post.getAuthorId());

                    // Since ONLY college students can create posts, get college profile
                    CollegeStudentProfile collegeProfile = userService.getCollegeProfile(authorUser.getId());
                    String authorName = collegeProfile.getName();

                    Long commentCount = (long) forumService.getCommentsByPost(post.getId()).size();
                    return ForumPostResponse.fromPost(post, authorName, authorUser.getEmail(), commentCount);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // Get all comments on a post (Anyone can view)
    @GetMapping("/post/{postId}/comments")
    public ResponseEntity<?> getCommentsByPost(@PathVariable Long postId) {
        List<ForumComment> comments = forumService.getCommentsByPost(postId);

        List<ForumCommentResponse> response = comments.stream()
                .map(comment -> {
                    // CORRECT: Get author by ID
                    User authorUser = userService.getUserById(comment.getAuthorId());

                    // Only college students can comment
                    CollegeStudentProfile collegeProfile = userService.getCollegeProfile(authorUser.getId());
                    String authorName = collegeProfile.getName();

                    return ForumCommentResponse.fromComment(comment, authorName, authorUser.getEmail());
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}