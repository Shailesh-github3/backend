package com.edubridge.backend.repository;

import com.edubridge.backend.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Get all pending reports (for admin dashboard)
    Page<Report> findByStatus(Report.ReportStatus status, Pageable pageable);

    // Get reports by user (for their reporting history)
    List<Report> findByReporterId(Long reporterId);

    // Check if user already reported this content (prevent spam)
    boolean existsByReporterIdAndTargetTypeAndTargetId(
            Long reporterId, String targetType, Long targetId);
}