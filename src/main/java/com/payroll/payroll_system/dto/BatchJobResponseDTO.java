package com.payroll.payroll_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchJobResponseDTO {
    
    private UUID batchJobId;
    
    private String jobType;
    
    private String status;
    
    private LocalDateTime submittedAt;
    
    private LocalDateTime startedAt;
    
    private LocalDateTime completedAt;
    
    private Integer totalRequests;
    
    private Integer processedRequests;
    
    private Integer successfulRequests;
    
    private Integer failedRequests;
    
    private String callbackUrl;
    
    private UUID requestedBy;
    
    private String description;
} 