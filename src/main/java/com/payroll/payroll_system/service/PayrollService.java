package com.payroll.payroll_system.service;

import com.payroll.payroll_system.constant.PayrollStatus;
import com.payroll.payroll_system.dto.PayrollDTO;
import com.payroll.payroll_system.entity.Attendance;
import com.payroll.payroll_system.entity.Payroll;
import com.payroll.payroll_system.repository.ActivityRepository;
import com.payroll.payroll_system.repository.AttendanceRepository;
import com.payroll.payroll_system.repository.EmployeeRepository;
import com.payroll.payroll_system.repository.OrganizationRepository;
import com.payroll.payroll_system.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class PayrollService {
    @Autowired
    private PayrollRepository payrollRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public List<PayrollDTO> getAllPayrolls() {
        return payrollRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PayrollDTO> getPayrollsByEmployeeId(UUID employeeId) {
        return payrollRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PayrollDTO> getPayrollsByOrganization(UUID organizationId) {
        return payrollRepository.findByEmployeeOrganizationId(organizationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PayrollDTO> getPayrollsByEmployeeIdAndOrganization(UUID employeeId, UUID organizationId) {
        return payrollRepository.findByEmployeeIdAndEmployeeOrganizationId(employeeId, organizationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<PayrollDTO> generatePayroll(UUID employeeId, LocalDate startDate, LocalDate endDate, UUID organizationId) {
        return employeeRepository.findByIdAndOrganizationId(employeeId, organizationId)
                .map(employee -> {
                    // Calculate working hours
                    LocalDateTime startDateTime = startDate.atStartOfDay();
                    LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

                    List<Attendance> attendances = attendanceRepository
                            .findByEmployeeIdAndCheckInBetween(employeeId, startDateTime, endDateTime);

                    // Calculate regular hours and overtime
                    long totalWorkMinutes = 0;
                    for (Attendance attendance : attendances) {
                        if (attendance.getCheckOut() != null) {
                            totalWorkMinutes += ChronoUnit.MINUTES.between(attendance.getCheckIn(), attendance.getCheckOut());
                        }
                    }

                    // Standard working hours per month (assuming 8 hours per day, 22 working days)
                    long standardWorkMinutes = 8 * 60 * 22;
                    long overtimeMinutes = Math.max(0, totalWorkMinutes - standardWorkMinutes);

                    // Calculate overtime payment (assuming 1.5x rate)
                    BigDecimal hourlyRate = employee.getBaseSalary().divide(BigDecimal.valueOf(standardWorkMinutes / 60), 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal overtimePay = hourlyRate.multiply(BigDecimal.valueOf(1.5)).multiply(BigDecimal.valueOf(overtimeMinutes / 60.0));

                    // Create payroll record
                    Payroll payroll = new Payroll();
                    payroll.setEmployee(employee);
                    payroll.setPayPeriodStart(startDate);
                    payroll.setPayPeriodEnd(endDate);
                    payroll.setBasicSalary(employee.getBaseSalary());
                    payroll.setOvertime(overtimePay);
                    payroll.setDeductions(BigDecimal.ZERO); // Placeholder for tax calculations
                    payroll.setBonus(BigDecimal.ZERO); // Placeholder for bonus calculations
                    payroll.setNetSalary(employee.getBaseSalary().add(overtimePay)); // Simplified calculation
                    payroll.setStatus(PayrollStatus.DRAFT);
                    payroll.setProcessedAt(LocalDateTime.now());

                    return convertToDTO(payrollRepository.save(payroll));
                });
    }

    @Transactional
    public Optional<PayrollDTO> updatePayrollStatus(UUID payrollId, PayrollStatus status, UUID organizationId) {
        return payrollRepository.findByIdAndEmployeeOrganizationId(payrollId, organizationId)
                .map(payroll -> {
                    payroll.setStatus(status);
                    return convertToDTO(payrollRepository.save(payroll));
                });
    }

    private PayrollDTO convertToDTO(Payroll payroll) {
          PayrollDTO payrollDTO = new PayrollDTO();
            payrollDTO.setId(payroll.getId());
            payrollDTO.setEmployeeId(payroll.getEmployee().getId());
            payrollDTO.setEmployeeName(payroll.getEmployee().getFirstName() + " " + payroll.getEmployee().getLastName());
            payrollDTO.setPayPeriodStart(payroll.getPayPeriodStart());
            payrollDTO.setPayPeriodEnd(payroll.getPayPeriodEnd());
            payrollDTO.setBasicSalary(payroll.getBasicSalary());
            payrollDTO.setOvertime(payroll.getOvertime());
            payrollDTO.setDeductions(payroll.getDeductions());
            payrollDTO.setBonus(payroll.getBonus());
            payrollDTO.setNetSalary(payroll.getNetSalary());
            payrollDTO.setStatus(payroll.getStatus());
            payrollDTO.setProcessedAt(payroll.getProcessedAt());
            return payrollDTO;
    }
}
