package com.payroll.payroll_system.repository;

import com.payroll.payroll_system.entity.Advance;
import com.payroll.payroll_system.entity.Employee;
import com.payroll.payroll_system.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AdvanceRepository extends JpaRepository<Advance, UUID> {
    List<Advance> findByEmployee(Employee employee);
    List<Advance> findByOrganization(Organization organization);
    List<Advance> findByStatus(String status);
    List<Advance> findByEmployeeAndStatus(Employee employee, String status);
    List<Advance> findByOrganizationAndStatus(Organization organization, String status);
    List<Advance> findByRequestDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Advance> findByRepaymentDateBefore(LocalDateTime date);
    List<Advance> findByEmployeeAndFullyRepaid(Employee employee, boolean fullyRepaid);
} 