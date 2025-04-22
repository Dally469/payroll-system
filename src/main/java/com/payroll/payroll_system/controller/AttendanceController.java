package com.payroll.payroll_system.controller;
import com.payroll.payroll_system.dto.AttendanceDTO;
import com.payroll.payroll_system.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/attendance")
 public class AttendanceController {
    @Autowired
    private  AttendanceService attendanceService;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceDTO>> getAttendanceByEmployeeId(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByEmployeeId(employeeId));
    }

    @PostMapping("/check-in/{employeeId}")
    public ResponseEntity<AttendanceDTO> checkIn(@PathVariable UUID employeeId) {
        return attendanceService.recordCheckIn(employeeId)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/check-out/{attendanceId}")
    public ResponseEntity<AttendanceDTO> checkOut(@PathVariable UUID attendanceId) {
        return attendanceService.recordCheckOut(attendanceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
