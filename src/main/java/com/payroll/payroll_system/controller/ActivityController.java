package com.payroll.payroll_system.controller;

import com.payroll.payroll_system.dto.ActivityCreateDTO;
import com.payroll.payroll_system.dto.ActivityDTO;
import com.payroll.payroll_system.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
 public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByEmployeeId(@PathVariable UUID employeeId) {
        return ResponseEntity.ok(activityService.getActivitiesByEmployeeId(employeeId));
    }

    @PostMapping("/start/{employeeId}")
    public ResponseEntity<ActivityDTO> startActivity(
            @PathVariable UUID employeeId,
            @RequestBody ActivityCreateDTO activityDTO) {
        return activityService.startActivity(employeeId, activityDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/end/{activityId}")
    public ResponseEntity<ActivityDTO> endActivity(@PathVariable UUID activityId) {
        return activityService.endActivity(activityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
