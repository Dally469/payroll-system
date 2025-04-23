package com.payroll.payroll_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAdvanceResponseDTO {
    
    private UUID batchId;
    
    private UUID requestedBy;
    
    private LocalDateTime requestedAt;
    
    private String status;
    
    private int totalRequests;
    
    private int processedRequests;
    
    private int failedRequests;
    
    private List<AdvanceResponseDTO> results;
    
    private String description;
    
    private LocalDateTime completedAt;
    
    private String callbackUrl;
} 