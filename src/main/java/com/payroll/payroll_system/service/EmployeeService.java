package com.payroll.payroll_system.service;

import com.payroll.payroll_system.dto.EmployeeDTO;
import com.payroll.payroll_system.entity.Employee;
import com.payroll.payroll_system.repository.DepartmentRepository;
import com.payroll.payroll_system.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = convertToEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    @Transactional
    public Optional<EmployeeDTO> updateEmployee(UUID id, EmployeeDTO employeeDTO) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    employee.setFirstName(employeeDTO.getFirstName());
                    employee.setLastName(employeeDTO.getLastName());
                    employee.setEmail(employeeDTO.getEmail());
                    employee.setBaseSalary(employeeDTO.getBaseSalary());

                    if (employeeDTO.getDepartmentId() != null) {
                        departmentRepository.findById(employeeDTO.getDepartmentId())
                                .ifPresent(employee::setDepartment);
                    }

                    return convertToDTO(employeeRepository.save(employee));
                });
    }

    @Transactional
    public boolean deleteEmployee(UUID id) {
        if (employeeRepository.existsById(id)) {
            employeeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private EmployeeDTO convertToDTO(Employee employee) {
          EmployeeDTO employeeDTO =  new EmployeeDTO();
            employeeDTO.setId(employee.getId());
            employeeDTO.setFirstName(employee.getFirstName());
            employeeDTO.setLastName(employee.getLastName());
            employeeDTO.setEmail(employee.getEmail());
            employeeDTO.setDateOfJoining(employee.getDateOfJoining());
            employeeDTO.setBaseSalary(employee.getBaseSalary());
            employeeDTO.setDepartmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null);
            employeeDTO.setDepartmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null);
            return  employeeDTO;
    }

    private Employee convertToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setDateOfJoining(dto.getDateOfJoining());
        employee.setBaseSalary(dto.getBaseSalary());

        if (dto.getDepartmentId() != null) {
            departmentRepository.findById(dto.getDepartmentId())
                    .ifPresent(employee::setDepartment);
        }

        return employee;
    }
}