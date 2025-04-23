package com.payroll.payroll_system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
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
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvalDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime requestDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime repaymentDate;
    
    private boolean fullyRepaid;
    private BigDecimal repaidAmount;
    private BigDecimal remainingAmount;
} 