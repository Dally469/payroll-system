package com.payroll.payroll_system.service;
import com.payroll.payroll_system.dto.AttendanceDTO;
import com.payroll.payroll_system.entity.Attendance;
import com.payroll.payroll_system.repository.AttendanceRepository;
import com.payroll.payroll_system.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAttendanceByEmployeeId(UUID employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<AttendanceDTO> recordCheckIn(UUID employeeId) {
        return employeeRepository.findById(employeeId)
                .map(employee -> {
                    Attendance attendance = new Attendance();
                    attendance.setEmployee(employee);
                    attendance.setCheckIn(LocalDateTime.now());
                    return convertToDTO(attendanceRepository.save(attendance));
                });
    }

    @Transactional
    public Optional<AttendanceDTO> recordCheckOut(UUID attendanceId) {
        return attendanceRepository.findById(attendanceId)
                .map(attendance -> {
                    attendance.setCheckOut(LocalDateTime.now());
                    return convertToDTO(attendanceRepository.save(attendance));
                });
    }

    private AttendanceDTO convertToDTO(Attendance attendance) {
         AttendanceDTO attendanceDTO = new AttendanceDTO();
        attendanceDTO.setId(attendance.getId());
        attendanceDTO.setEmployeeId(attendance.getEmployee().getId());
        attendanceDTO.setEmployeeName(attendance.getEmployee().getFirstName() + " " + attendance.getEmployee().getLastName());
        attendanceDTO.setCheckIn(attendance.getCheckIn());
        attendanceDTO.setCheckOut(attendance.getCheckOut());
        attendanceDTO.setDurationInMinutes(attendance.getDurationInMinutes());
        return  attendanceDTO;

    }
}