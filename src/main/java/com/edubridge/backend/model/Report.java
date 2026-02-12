package com.edubridge.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reporter_id", nullable = false)
    private Long reporterId;  // User who reported

    @Column(name = "target_type", nullable = false)
    private String targetType; // "POST" or "COMMENT"

    @Column(name = "target_id", nullable = false)
    private Long targetId;     // ID of post/comment being reported

    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;     // Why it was reported

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "resolved_by")
    private Long resolvedBy;   // Admin who handled it

    @Column(name = "resolved_at")
    private Date resolvedAt;

    @Column(name = "resolution_note")
    private String resolutionNote;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public enum ReportStatus {
        PENDING,    // Awaiting admin review
        RESOLVED,   // Action taken (content deleted)
        DISMISSED   // False report, no action
    }
}