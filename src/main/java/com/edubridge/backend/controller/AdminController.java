package com.edubridge.backend.controller;




import com.edubridge.backend.model.Report;
import com.edubridge.backend.model.User;
import com.edubridge.backend.service.ForumService;
import com.edubridge.backend.service.ReportService;
import com.edubridge.backend.service.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ForumService forumService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserService userService;

    @Data
    public static class ModerationAction {
        private String reason;
    }

    // ✅ DELETE ABUSIVE POST
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/forum/post/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long postId,
            @RequestBody(required = false) ModerationAction action) {

        forumService.deletePost(postId);

        String reason = (action != null && action.getReason() != null)
                ? action.getReason()
                : "Violated community guidelines";

        // Log moderation action (in production, save to audit table)
        System.out.println("🚨 ADMIN ACTION: Post " + postId + " deleted. Reason: " + reason);

        return ResponseEntity.ok(Map.of(
                "message", "Post deleted successfully",
                "postId", postId,
                "reason", reason
        ));
    }

    // ✅ DELETE ABUSIVE COMMENT
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/forum/comment/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            @RequestBody(required = false) ModerationAction action) {

        forumService.deleteComment(commentId);

        String reason = (action != null && action.getReason() != null)
                ? action.getReason()
                : "Violated community guidelines";

        System.out.println("🚨 ADMIN ACTION: Comment " + commentId + " deleted. Reason: " + reason);

        return ResponseEntity.ok(Map.of(
                "message", "Comment deleted successfully",
                "commentId", commentId,
                "reason", reason
        ));
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getReportedContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Report> reports = reportService.getPendingReports(page, size);

        return ResponseEntity.ok(Map.of(
                "content", reports.getContent(),
                "page", page,
                "size", size,
                "totalElements", reports.getTotalElements(),
                "totalPages", reports.getTotalPages(),
                "hasNext", reports.hasNext()
        ));
    }

    @PostMapping("/reports/{reportId}/resolve")
    public ResponseEntity<?> resolveReport(
            @PathVariable Long reportId,
            @RequestBody ResolveReportRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {

        String email = currentUser.getUsername();
        User adminUser = userService.getUserByEmail(email);

        Report report = reportService.resolveReport(
                reportId,
                adminUser.getId(),
                request.getAction(),      // "DELETE" or "DISMISS"
                request.getNote()         // Resolution note
        );

        return ResponseEntity.ok(Map.of(
                "message", "Report resolved successfully",
                "reportId", report.getId(),
                "status", report.getStatus(),
                "action", request.getAction(),
                "resolutionNote", report.getResolutionNote()
        ));
    }

    @Data
    public static class ResolveReportRequest {
        @NotBlank(message = "Action is required (DELETE or DISMISS)")
        private String action;
        private String note; // Optional resolution note
    }
}
