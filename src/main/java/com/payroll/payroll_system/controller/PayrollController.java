package com.payroll.payroll_system.controller;

import com.payroll.payroll_system.dto.PayrollDTO;
import com.payroll.payroll_system.dto.PayrollGenerateRequestDTO;
import com.payroll.payroll_system.dto.PayrollStatusUpdateDTO;
import com.payroll.payroll_system.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payrolls")
public class PayrollController {
    @Autowired
    private PayrollService payrollService;

    @GetMapping
    public ResponseEntity<List<PayrollDTO>> getAllPayrolls() {
        return ResponseEntity.ok(payrollService.getAllPayrolls());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PayrollDTO>> getPayrollsByEmployeeId(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(payrollService.getPayrollsByEmployeeId(employeeId));
    }

    @PostMapping("/generate")
    public ResponseEntity<PayrollDTO> generatePayroll(@RequestBody PayrollGenerateRequestDTO request) {
        return payrollService.generatePayroll(request.getEmployeeId(), request.getStartDate(), request.getEndDate())
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{payrollId}/status")
    public ResponseEntity<PayrollDTO> updatePayrollStatus(
            @PathVariable UUID payrollId,
            @RequestBody PayrollStatusUpdateDTO request) {
        return payrollService.updatePayrollStatus(payrollId, request.getStatus())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
