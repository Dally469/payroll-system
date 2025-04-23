package com.payroll.payroll_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceResponseDTO {
    private UUID id;
    private UUID employeeId;
    private BigDecimal amount;
    private String status;
    private String reason;
    private LocalDateTime requestDate;
    private LocalDateTime repaymentDate;
    private LocalDateTime processedAt;
    private String message; // For error messages or additional info
} 