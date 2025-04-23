package com.payroll.payroll_system.controller;

import com.payroll.payroll_system.dto.ActivityCreateDTO;
import com.payroll.payroll_system.dto.ActivityDTO;
import com.payroll.payroll_system.dto.ApiResponse;
import com.payroll.payroll_system.entity.User;
import com.payroll.payroll_system.service.ActivityService;
import com.payroll.payroll_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private final ActivityService activityService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getActivitiesByOrganization(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.ok(ApiResponse.success(List.of(), "No organization assigned"));
        }
        List<ActivityDTO> activities = activityService.getActivitiesByOrganization(currentUser.getOrganization().getId());
        return ResponseEntity.ok(ApiResponse.success(activities, "Activities retrieved successfully"));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getActivitiesByEmployeeId(
            @PathVariable UUID employeeId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        List<ActivityDTO> activities = activityService.getActivitiesByEmployeeIdAndOrganization(
                employeeId, currentUser.getOrganization().getId());
        
        return ResponseEntity.ok(ApiResponse.success(activities, "Employee activities retrieved successfully"));
    }

    @PostMapping("/start/{employeeId}")
    public ResponseEntity<ApiResponse<ActivityDTO>> startActivity(
            @PathVariable UUID employeeId,
            @RequestBody ActivityCreateDTO activityDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        return activityService.startActivityForOrganization(
                employeeId, activityDTO, currentUser.getOrganization().getId())
                .map(activity -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(activity, "Activity started successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Employee not found")));
    }

    @PutMapping("/end/{activityId}")
    public ResponseEntity<ApiResponse<ActivityDTO>> endActivity(
            @PathVariable UUID activityId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        if (currentUser.getOrganization() == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied, no organization assigned"));
        }
        
        return activityService.endActivityForOrganization(activityId, currentUser.getOrganization().getId())
                .map(activity -> ResponseEntity.ok(ApiResponse.success(activity, "Activity ended successfully")))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Activity not found")));
    }
}
