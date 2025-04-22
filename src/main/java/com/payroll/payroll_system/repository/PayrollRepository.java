package com.payroll.payroll_system.repository;

import com.payroll.payroll_system.constant.PayrollStatus;
import com.payroll.payroll_system.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, UUID> {
    List<Payroll> findByEmployeeId(UUID employeeId);

    List<Payroll> findByPayPeriodStartGreaterThanEqualAndPayPeriodEndLessThanEqual(
            LocalDate start, LocalDate end);

    List<Payroll> findByStatus(PayrollStatus status);
}
