package com.payroll.payroll_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class BatchPayrollRequestDTO {
    
    @NotNull(message = "Requester ID cannot be null")
    private UUID requestedBy;
    
    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();
    
    @NotEmpty(message = "At least one payroll request must be provided")
    @Size(min = 1, max = 1000, message = "Number of payroll requests must be between 1 and 1000")
    private List<@Valid PayrollRequestDTO> payrolls;
    
    private String payPeriod;
    
    private String description;
    
    private String callbackUrl;
    
    @Builder.Default
    private boolean skipValidation = false;
} 