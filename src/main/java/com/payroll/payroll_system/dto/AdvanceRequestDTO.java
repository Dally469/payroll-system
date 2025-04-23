package com.payroll.payroll_system.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdvanceRequestDTO {
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    @NotNull(message = "Request date is required")
    private LocalDate requestDate;
    
    @NotNull(message = "Repayment date is required")
    @FutureOrPresent(message = "Repayment date must be in present or future")
    private LocalDate repaymentDate;
} 