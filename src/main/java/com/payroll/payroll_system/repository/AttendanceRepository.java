package com.payroll.payroll_system.repository;

import com.payroll.payroll_system.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findByEmployeeId(UUID employeeId);

    List<Attendance> findByEmployeeIdAndCheckInBetween(
            UUID employeeId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(a.checkOut - a.checkIn) FROM Attendance a " +
            "WHERE a.employee.id = :employeeId AND a.checkIn >= :startDate AND a.checkOut <= :endDate")
    Long getTotalWorkingTimeForEmployee(
            @Param("employeeId") UUID employeeId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
