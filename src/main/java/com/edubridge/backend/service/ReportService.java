package com.edubridge.backend.service;


import com.edubridge.backend.exception.BadRequestException;
import com.edubridge.backend.exception.ResourceNotFoundException;
import com.edubridge.backend.model.Report;
import com.edubridge.backend.repository.ReportRepository;
import com.edubridge.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForumService forumService;

    // ✅ USER: Report content
    @Transactional
    public Report reportContent(Long reporterId, String targetType, Long targetId, String reason) {
        // Validate target type
        if (!targetType.equals("POST") && !targetType.equals("COMMENT")) {
            throw new BadRequestException("Invalid target type. Must be 'POST' or 'COMMENT'");
        }

        // Prevent duplicate reports from same user
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(
                reporterId, targetType, targetId)) {
            throw new BadRequestException("You have already reported this content");
        }

        // Verify target exists (optional but good practice)
        if (targetType.equals("POST")) {
            forumService.getPostById(targetId); // Will throw exception if not found
        } else {
            forumService.getCommentById(targetId);
        }

        Report report = new Report();
        report.setReporterId(reporterId);
        report.setTargetType(targetType);
        report.setTargetId(targetId);
        report.setReason(reason);
        report.setStatus(Report.ReportStatus.PENDING);

        return reportRepository.save(report);
    }

    // ✅ ADMIN: Get all pending reports (with pagination)
    public Page<Report> getPendingReports(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return reportRepository.findByStatus(Report.ReportStatus.PENDING, pageable);
    }

    // ✅ ADMIN: Resolve report (take action)
    @Transactional
    public Report resolveReport(Long reportId, Long adminId, String action, String note) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        if (report.getStatus() != Report.ReportStatus.PENDING) {
            throw new BadRequestException("Report is already resolved");
        }

        // Take action based on admin decision
        if (action.equals("DELETE")) {
            // Delete the reported content
            if (report.getTargetType().equals("POST")) {
                forumService.deletePost(report.getTargetId());
            } else {
                forumService.deleteComment(report.getTargetId());
            }
            report.setStatus(Report.ReportStatus.RESOLVED);
        } else if (action.equals("DISMISS")) {
            report.setStatus(Report.ReportStatus.DISMISSED);
        } else {
            throw new BadRequestException("Invalid action. Must be 'DELETE' or 'DISMISS'");
        }

        report.setResolvedBy(adminId);
        report.setResolvedAt(new Date());
        report.setResolutionNote(note != null ? note : "No note provided");

        // Audit log
        System.out.println(String.format(
                "🚨 ADMIN ACTION: Report #%d resolved by admin #%d. Action: %s. Target: %s #%d",
                reportId, adminId, action, report.getTargetType(), report.getTargetId()
        ));

        return reportRepository.save(report);
    }

    // ✅ USER: Get their reporting history
    public List<Report> getUserReports(Long userId) {
        return reportRepository.findByReporterId(userId);
    }
}
