package com.payroll.payroll_system.repository;

import com.payroll.payroll_system.constant.ActivityType;
import com.payroll.payroll_system.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    List<Activity> findByEmployeeId(UUID employeeId);

    List<Activity> findByEmployeeIdAndStartTimeBetween(
            UUID employeeId, LocalDateTime start, LocalDateTime end);

    List<Activity> findByEmployeeIdAndType(UUID employeeId, ActivityType type);
}