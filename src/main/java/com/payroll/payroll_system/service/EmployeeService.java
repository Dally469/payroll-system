package com.payroll.payroll_system.service;

import com.payroll.payroll_system.dto.EmployeeDTO;
import com.payroll.payroll_system.entity.Employee;
import com.payroll.payroll_system.entity.Organization;
import com.payroll.payroll_system.repository.DepartmentRepository;
import com.payroll.payroll_system.repository.EmployeeRepository;
import com.payroll.payroll_system.repository.OrganizationRepository;
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
    @Autowired
    private OrganizationRepository organizationRepository;

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

    @Transactional(readOnly = true)
    public List<EmployeeDTO> getEmployeesByOrganization(UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        return employeeRepository.findByOrganization(organization).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> getEmployeeByIdAndOrganization(UUID id, UUID organizationId) {
        return employeeRepository.findByIdAndOrganizationId(id, organizationId)
                .map(this::convertToDTO);
    }

    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        Employee employee = convertToEntity(employeeDTO);
        
        if (employeeDTO.getOrganizationId() != null) {
            organizationRepository.findById(employeeDTO.getOrganizationId())
                    .ifPresent(employee::setOrganization);
        }
        
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }

    @Transactional
    public Optional<EmployeeDTO> updateEmployee(UUID id, EmployeeDTO employeeDTO) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    updateEmployeeFromDTO(employee, employeeDTO);
                    return convertToDTO(employeeRepository.save(employee));
                });
    }
    
    @Transactional
    public Optional<EmployeeDTO> updateEmployeeForOrganization(UUID id, EmployeeDTO employeeDTO, UUID organizationId) {
        return employeeRepository.findByIdAndOrganizationId(id, organizationId)
                .map(employee -> {
                    updateEmployeeFromDTO(employee, employeeDTO);
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
    
    @Transactional
    public boolean deleteEmployeeForOrganization(UUID id, UUID organizationId) {
        Optional<Employee> employee = employeeRepository.findByIdAndOrganizationId(id, organizationId);
        if (employee.isPresent()) {
            employeeRepository.delete(employee.get());
            return true;
        }
        return false;
    }
    
    private void updateEmployeeFromDTO(Employee employee, EmployeeDTO employeeDTO) {
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setBaseSalary(employeeDTO.getBaseSalary());

        if (employeeDTO.getDepartmentId() != null) {
            departmentRepository.findById(employeeDTO.getDepartmentId())
                    .ifPresent(employee::setDepartment);
        }
    }

    private EmployeeDTO convertToDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setEmail(employee.getEmail());
        employeeDTO.setDateOfJoining(employee.getDateOfJoining());
        employeeDTO.setBaseSalary(employee.getBaseSalary());
        
        if (employee.getDepartment() != null) {
            employeeDTO.setDepartmentId(employee.getDepartment().getId());
            employeeDTO.setDepartmentName(employee.getDepartment().getName());
        }
        
        if (employee.getOrganization() != null) {
            employeeDTO.setOrganizationId(employee.getOrganization().getId());
            employeeDTO.setOrganizationName(employee.getOrganization().getName());
        }
        
        return employeeDTO;
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