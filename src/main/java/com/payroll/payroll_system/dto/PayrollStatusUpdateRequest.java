package com.payroll.payroll_system.dto;

import com.payroll.payroll_system.constant.PayrollStatus;

import java.util.UUID;

public class PayrollStatusUpdateRequest {
    private UUID payrollId;
    private PayrollStatus status;

    public UUID getPayrollId() {
        return payrollId;
    }

    public void setPayrollId(UUID payrollId) {
        this.payrollId = payrollId;
    }

    public PayrollStatus getStatus() {
        return status;
    }

    public void setStatus(PayrollStatus status) {
        this.status = status;
    }
}
