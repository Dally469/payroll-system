package com.payroll.payroll_system.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollRequestDTO {
    
    @NotNull(message = "Employee ID is required")
    private UUID employeeId;
    
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
    
    @NotNull(message = "End date is required")
    private LocalDate endDate;
    
    @Positive(message = "Base salary must be positive")
    private BigDecimal baseSalary;
    
    private BigDecimal overtime;
    
    private BigDecimal bonus;
    
    private BigDecimal deductions;
    
    private String notes;
} 