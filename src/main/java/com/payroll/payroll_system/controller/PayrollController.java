package com.payroll.payroll_system.controller;

import com.payroll.payroll_system.dto.ApiResponse;
import com.payroll.payroll_system.dto.PayrollDTO;
import com.payroll.payroll_system.dto.PayrollGenerateRequestDTO;
import com.payroll.payroll_system.dto.PayrollStatusUpdateDTO;
import com.payroll.payroll_system.entity.User;
import com.payroll.payroll_system.service.PayrollService;
import com.payroll.payroll_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payrolls")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService payrollService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PayrollDTO>>> getPayrollsByOrganization(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.ok(ApiResponse.success(List.of(), "No organization assigned"));
        }
        List<PayrollDTO> payrolls = payrollService.getPayrollsByOrganization(currentUser.getOrganization().getId());
        return ResponseEntity.ok(ApiResponse.success(payrolls, "Payrolls retrieved successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<PayrollDTO>>> getPayrollsByEmployeeId(
            @PathVariable UUID employeeId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        List<PayrollDTO> payrolls = payrollService.getPayrollsByEmployeeIdAndOrganization(
                employeeId, currentUser.getOrganization().getId());
        
        return ResponseEntity.ok(ApiResponse.success(payrolls, "Employee payrolls retrieved successfully"));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<PayrollDTO>> generatePayroll(
            @RequestBody PayrollGenerateRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        return payrollService.generatePayroll(
                request.getEmployeeId(), 
                request.getStartDate(), 
                request.getEndDate(),
                currentUser.getOrganization().getId())
                .map(payroll -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(payroll, "Payroll generated successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Employee not found")));
    }

    @PutMapping("/{payrollId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<PayrollDTO>> updatePayrollStatus(
            @PathVariable UUID payrollId,
            @RequestBody PayrollStatusUpdateDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        return payrollService.updatePayrollStatus(
                payrollId, 
                request.getStatus(),
                currentUser.getOrganization().getId())
                .map(payroll -> ResponseEntity.ok(ApiResponse.success(payroll, "Payroll status updated successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Payroll not found")));
    }
}
