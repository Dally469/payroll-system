package com.payroll.payroll_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvanceDTO {
    private UUID id;
    private UUID employeeId;
    private String employeeName;
    private UUID organizationId;
    private String organizationName;
    private BigDecimal amount;
    private String reason;
    private String status;
    private String rejectionReason;
    private UUID approvedById;
    private String approvedByName;
    private LocalDateTime approvalDate;
    private LocalDate requestDate;
    private LocalDate repaymentDate;
    private boolean fullyRepaid;
    private BigDecimal repaidAmount;
    private BigDecimal remainingAmount;
} 