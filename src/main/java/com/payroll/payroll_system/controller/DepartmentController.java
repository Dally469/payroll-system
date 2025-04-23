package com.payroll.payroll_system.controller;

import com.payroll.payroll_system.dto.ApiResponse;
import com.payroll.payroll_system.dto.DepartmentDTO;
import com.payroll.payroll_system.entity.User;
import com.payroll.payroll_system.service.DepartmentService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getDepartmentsByOrganization(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.ok(ApiResponse.success(List.of(), "No organization assigned"));
        }
        List<DepartmentDTO> departments = departmentService.getDepartmentsByOrganization(currentUser.getOrganization().getId());
        return ResponseEntity.ok(ApiResponse.success(departments, "Departments retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartmentById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        return departmentService.getDepartmentByIdAndOrganization(id, currentUser.getOrganization().getId())
                .map(department -> ResponseEntity.ok(ApiResponse.success(department, "Department retrieved successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Department not found")));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(
            @RequestBody DepartmentDTO departmentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        departmentDTO.setOrganizationId(currentUser.getOrganization().getId());
        DepartmentDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdDepartment, "Department created successfully"));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<DepartmentDTO>> updateDepartment(
            @PathVariable UUID id,
            @RequestBody DepartmentDTO departmentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        return departmentService.updateDepartmentForOrganization(id, departmentDTO, currentUser.getOrganization().getId())
                .map(updatedDepartment -> ResponseEntity.ok(ApiResponse.success(updatedDepartment, "Department updated successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Department not found")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        boolean deleted = departmentService.deleteDepartmentForOrganization(id, currentUser.getOrganization().getId());
        
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, "Department deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Department not found"));
        }
    }
}