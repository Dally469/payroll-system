package com.payroll.payroll_system.dto;

import com.payroll.payroll_system.constant.PayrollStatus;
import lombok.*;


public class PayrollStatusUpdateDTO {
    private PayrollStatus status;

    public PayrollStatus getStatus() {
        return status;
    }

    public void setStatus(PayrollStatus status) {
        this.status = status;
    }
}
