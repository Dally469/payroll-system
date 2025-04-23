package com.payroll.payroll_system.controller;

import com.payroll.payroll_system.dto.AdvanceApprovalDTO;
import com.payroll.payroll_system.dto.AdvanceDTO;
import com.payroll.payroll_system.dto.AdvanceRequestDTO;
import com.payroll.payroll_system.dto.ApiResponse;
import com.payroll.payroll_system.entity.User;
import com.payroll.payroll_system.service.AdvanceService;
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
@RequestMapping("/api/advances")
@RequiredArgsConstructor
public class AdvanceController {
    private final AdvanceService advanceService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AdvanceDTO>>> getAdvancesByOrganization(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.ok(ApiResponse.success(List.of(), "No organization assigned"));
        }
        
        UUID organizationId = currentUser.getOrganization().getId();
        List<AdvanceDTO> advances;
        
        if (status != null) {
            advances = advanceService.getAdvancesByOrganizationAndStatus(organizationId, status);
            return ResponseEntity.ok(ApiResponse.success(advances, "Advances with status '" + status + "' retrieved successfully"));
        } else {
            advances = advanceService.getAdvancesByOrganization(organizationId);
            return ResponseEntity.ok(ApiResponse.success(advances, "All advances retrieved successfully"));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<AdvanceDTO>>> getAdvancesByEmployee(
            @PathVariable UUID employeeId,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        UUID organizationId = currentUser.getOrganization().getId();
        List<AdvanceDTO> advances;
        
        if (status != null) {
            advances = advanceService.getAdvancesByEmployeeAndStatusAndOrganization(
                    employeeId, status, organizationId);
            return ResponseEntity.ok(ApiResponse.success(advances, 
                    "Employee advances with status '" + status + "' retrieved successfully"));
        } else {
            advances = advanceService.getAdvancesByEmployeeAndOrganization(
                    employeeId, organizationId);
            return ResponseEntity.ok(ApiResponse.success(advances, "Employee advances retrieved successfully"));
        }
    }

    @GetMapping("/{advanceId}")
    public ResponseEntity<ApiResponse<AdvanceDTO>> getAdvanceById(
            @PathVariable UUID advanceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        return advanceService.getAdvanceByIdAndOrganization(advanceId, currentUser.getOrganization().getId())
                .map(advance -> ResponseEntity.ok(ApiResponse.success(advance, "Advance retrieved successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Advance not found")));
    }

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<AdvanceDTO>> requestAdvance(
            @RequestBody AdvanceRequestDTO requestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        UUID organizationId = currentUser.getOrganization().getId();
        AdvanceDTO advanceDTO = advanceService.requestAdvance(requestDTO, organizationId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(advanceDTO, "Advance requested successfully"));
    }

    @PutMapping("/{advanceId}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<AdvanceDTO>> approveAdvance(
            @PathVariable UUID advanceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        UUID organizationId = currentUser.getOrganization().getId();
        return advanceService.approveAdvance(advanceId, currentUser.getId(), organizationId)
                .map(advance -> ResponseEntity.ok(ApiResponse.success(advance, "Advance approved successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Advance not found")));
    }

    @PutMapping("/{advanceId}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<AdvanceDTO>> rejectAdvance(
            @PathVariable UUID advanceId,
            @RequestBody AdvanceApprovalDTO approvalDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        UUID organizationId = currentUser.getOrganization().getId();
        return advanceService.rejectAdvance(advanceId, approvalDTO.getReason(), organizationId)
                .map(advance -> ResponseEntity.ok(ApiResponse.success(advance, "Advance rejected successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Advance not found")));
    }

    @PutMapping("/{advanceId}/repay")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<AdvanceDTO>> recordRepayment(
            @PathVariable UUID advanceId,
            @RequestParam double amount,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        UUID organizationId = currentUser.getOrganization().getId();
        return advanceService.recordRepayment(advanceId, amount, organizationId)
                .map(advance -> ResponseEntity.ok(ApiResponse.success(advance, "Repayment recorded successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Advance not found")));
    }
} 