package com.payroll.payroll_system.controller;

import com.payroll.payroll_system.dto.*;
import com.payroll.payroll_system.entity.User;
import com.payroll.payroll_system.service.BatchJobService;
import com.payroll.payroll_system.service.UserService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchController {
    
    private final BatchJobService batchJobService;
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<List<BatchJobResponseDTO>>> getBatchJobs(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        List<BatchJobResponseDTO> jobs = batchJobService.getBatchJobsByOrganization(
                currentUser.getOrganization().getId());
        
        return ResponseEntity.ok(ApiResponse.success(jobs, "Batch jobs retrieved successfully"));
    }
    
    @GetMapping("/{batchJobId}")
    public ResponseEntity<ApiResponse<BatchJobResponseDTO>> getBatchJobById(
            @PathVariable UUID batchJobId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        return batchJobService.getBatchJobByIdAndOrganization(batchJobId, currentUser.getOrganization().getId())
                .map(job -> ResponseEntity.ok(ApiResponse.success(job, "Batch job retrieved successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Batch job not found")));
    }
    
    @PostMapping("/payroll")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<BatchJobResponseDTO>> processBatchPayroll(
            @Valid @RequestBody BatchPayrollRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        // Start processing asynchronously
        batchJobService.processBatchPayroll(request, currentUser.getOrganization().getId());
        
        // Create and return initial response
        BatchJobResponseDTO initialResponse = BatchJobResponseDTO.builder()
                .status("SUBMITTED")
                .requestedBy(request.getRequestedBy())
                .totalRequests(request.getPayrolls().size())
                .jobType("PAYROLL")
                .build();
        
        return ResponseEntity.accepted()
                .body(ApiResponse.success(initialResponse, "Batch payroll job submitted successfully"));
    }
    
    @PostMapping("/advances")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<BatchJobResponseDTO>> processBatchAdvances(
            @Valid @RequestBody BatchAdvanceRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        // Start processing asynchronously
        batchJobService.processBatchAdvances(request, currentUser.getOrganization().getId());
        
        // Create and return initial response
        BatchJobResponseDTO initialResponse = BatchJobResponseDTO.builder()
                .status("SUBMITTED")
                .requestedBy(request.getRequestedBy())
                .totalRequests(request.getRequests().size())
                .jobType("ADVANCE")
                .build();
        
        return ResponseEntity.accepted()
                .body(ApiResponse.success(initialResponse, "Batch advance job submitted successfully"));
    }
    
    @PostMapping("/advances/action")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<String>> batchAdvanceAction(
            @Valid @RequestBody BatchAdvanceActionDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        // For now, we'll just return a placeholder response
        // In a real implementation, you would process the batch action asynchronously
        
        return ResponseEntity.accepted()
                .body(ApiResponse.success(
                        "Batch job submitted with " + request.getAdvanceRequestIds().size() + " advance requests",
                        "Batch advance action job submitted"));
    }
} 