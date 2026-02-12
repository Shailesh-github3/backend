package com.edubridge.backend.controller;


import com.edubridge.backend.model.Report;
import com.edubridge.backend.model.User;
import com.edubridge.backend.service.ReportService;
import com.edubridge.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Data
    public static class ReportRequest {
        @NotBlank(message = "Reason is required")
        private String reason;
    }

    // ✅ Report a forum post
    @PostMapping("/post/{postId}")
    public ResponseEntity<?> reportPost(
            @PathVariable Long postId,
            @Valid @RequestBody ReportRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        String email = currentUser.getUsername();
        User user = userService.getUserByEmail(email);

        Report report = reportService.reportContent(
                user.getId(),
                "POST",
                postId,
                request.getReason()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Post reported successfully. Thank you for helping keep our community safe!",
                "reportId", report.getId(),
                "status", report.getStatus()
        ));
    }

    // ✅ Report a comment
    @PostMapping("/comment/{commentId}")
    public ResponseEntity<?> reportComment(
            @PathVariable Long commentId,
            @Valid @RequestBody ReportRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        String email = currentUser.getUsername();
        User user = userService.getUserByEmail(email);

        Report report = reportService.reportContent(
                user.getId(),
                "COMMENT",
                commentId,
                request.getReason()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Comment reported successfully. Thank you for helping keep our community safe!",
                "reportId", report.getId(),
                "status", report.getStatus()
        ));
    }

    // ✅ Get user's reporting history
    @GetMapping("/my-reports")
    public ResponseEntity<?> getUserReports(@AuthenticationPrincipal UserDetails currentUser) {
        String email = currentUser.getUsername();
        User user = userService.getUserByEmail(email);

        List<Report> reports = reportService.getUserReports(user.getId());
        return ResponseEntity.ok(reports);
    }
}
