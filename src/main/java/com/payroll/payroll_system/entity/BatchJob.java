package com.payroll.payroll_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "batch_jobs")
public class BatchJob {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private String jobType;
    
    @Column(nullable = false)
    private String status;
    
    @Column(nullable = false)
    private LocalDateTime submittedAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    @Column(nullable = false)
    private Integer totalRequests;
    
    private Integer processedRequests = 0;
    
    private Integer successfulRequests = 0;
    
    private Integer failedRequests = 0;
    
    private String callbackUrl;
    
    private String description;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requested_by")
    private User requestedBy;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;
    
    @Column(columnDefinition = "TEXT")
    private String resultDetails;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.processedRequests == null) {
            this.processedRequests = 0;
        }
        if (this.successfulRequests == null) {
            this.successfulRequests = 0;
        }
        if (this.failedRequests == null) {
            this.failedRequests = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 