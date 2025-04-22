package com.payroll.payroll_system.controller;

import com.payroll.payroll_system.dto.*;
import com.payroll.payroll_system.service.ActivityService;
import com.payroll.payroll_system.service.AttendanceService;
import com.payroll.payroll_system.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Optional;
import java.util.UUID;

@Controller
 public class WebSocketController {
    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PayrollService payrollService;

    // Attendance WebSocket handlers
    @MessageMapping("/attendance/check-in")
    @SendTo("/topic/attendance")
    public AttendanceDTO checkIn(UUID employeeId) {
        Optional<AttendanceDTO> result = attendanceService.recordCheckIn(employeeId);
        return result.orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));
    }

    @MessageMapping("/attendance/check-out")
    @SendTo("/topic/attendance")
    public AttendanceDTO checkOut(UUID attendanceId) {
        Optional<AttendanceDTO> result = attendanceService.recordCheckOut(attendanceId);
        return result.orElseThrow(() -> new RuntimeException("Attendance record not found: " + attendanceId));
    }

    // Activity WebSocket handlers
    @MessageMapping("/activity/start")
    @SendTo("/topic/activity")
    public ActivityDTO startActivity(ActivityWebSocketRequest request) {
        Optional<ActivityDTO> result = activityService.startActivity(
                request.getEmployeeId(), request.getActivity());
        return result.orElseThrow(() -> new RuntimeException("Employee not found: " + request.getEmployeeId()));
    }

    @MessageMapping("/activity/end")
    @SendTo("/topic/activity")
    public ActivityDTO endActivity(UUID activityId) {
        Optional<ActivityDTO> result = activityService.endActivity(activityId);
        return result.orElseThrow(() -> new RuntimeException("Activity not found: " + activityId));
    }

    // Payroll WebSocket handlers
    @MessageMapping("/payroll/generate")
    @SendTo("/topic/payroll")
    public PayrollDTO generatePayroll(PayrollGenerateRequestDTO request) {
        Optional<PayrollDTO> result = payrollService.generatePayroll(
                request.getEmployeeId(), request.getStartDate(), request.getEndDate());
        return result.orElseThrow(() -> new RuntimeException("Employee not found: " + request.getEmployeeId()));
    }

    @MessageMapping("/payroll/status-update")
    @SendTo("/topic/payroll")
    public PayrollDTO updatePayrollStatus(PayrollStatusUpdateRequest request) {
        Optional<PayrollDTO> result = payrollService.updatePayrollStatus(request.getPayrollId(), request.getStatus());
        return result.orElseThrow(() -> new RuntimeException("Payroll not found: " + request.getPayrollId()));
    }
}
