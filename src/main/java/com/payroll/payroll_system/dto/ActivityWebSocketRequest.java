package com.payroll.payroll_system.dto;


import java.util.UUID;

public class ActivityWebSocketRequest {
    private UUID employeeId;
    private ActivityCreateDTO activity;

    public UUID getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(UUID employeeId) {
        this.employeeId = employeeId;
    }

    public ActivityCreateDTO getActivity() {
        return activity;
    }

    public void setActivity(ActivityCreateDTO activity) {
        this.activity = activity;
    }
}
