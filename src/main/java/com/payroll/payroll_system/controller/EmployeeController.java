package com.payroll.payroll_system.controller;

import com.payroll.payroll_system.dto.ApiResponse;
import com.payroll.payroll_system.dto.EmployeeDTO;
import com.payroll.payroll_system.entity.User;
import com.payroll.payroll_system.service.EmployeeService;
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
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getEmployeesByOrganization(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.ok(ApiResponse.success(List.of(), "No organization assigned"));
        }
        List<EmployeeDTO> employees = employeeService.getEmployeesByOrganization(currentUser.getOrganization().getId());
        return ResponseEntity.ok(ApiResponse.success(employees, "Employees retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        return employeeService.getEmployeeByIdAndOrganization(id, currentUser.getOrganization().getId())
                .map(employee -> ResponseEntity.ok(ApiResponse.success(employee, "Employee retrieved successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Employee not found")));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<EmployeeDTO>> createEmployee(
            @RequestBody EmployeeDTO employeeDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        employeeDTO.setOrganizationId(currentUser.getOrganization().getId());
        EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdEmployee, "Employee created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<EmployeeDTO>> updateEmployee(
            @PathVariable UUID id,
            @RequestBody EmployeeDTO employeeDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        return employeeService.updateEmployeeForOrganization(id, employeeDTO, currentUser.getOrganization().getId())
                .map(updatedEmployee -> ResponseEntity.ok(ApiResponse.success(updatedEmployee, "Employee updated successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Employee not found")));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        boolean deleted = employeeService.deleteEmployeeForOrganization(id, currentUser.getOrganization().getId());
        
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, "Employee deleted successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Employee not found"));
        }
    }
}