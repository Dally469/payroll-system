package com.payroll.payroll_system.repository;

import com.payroll.payroll_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByDepartmentId(UUID departmentId);

    @Query("SELECT e FROM Employee e WHERE e.phone = :phone")
    Employee findByPhone(@Param("phone") String phone);
}