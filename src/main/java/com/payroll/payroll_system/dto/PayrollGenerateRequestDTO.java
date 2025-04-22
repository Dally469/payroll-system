package com.payroll.payroll_system.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

public class PayrollGenerateRequestDTO {
    private UUID employeeId;
    private LocalDate startDate;
    private LocalDate endDate;

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}

