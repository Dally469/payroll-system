package com.payroll.payroll_system.service;

import com.payroll.payroll_system.dto.DepartmentDTO;
import com.payroll.payroll_system.entity.Department;
import com.payroll.payroll_system.entity.Organization;
import com.payroll.payroll_system.repository.DepartmentRepository;
import com.payroll.payroll_system.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getDepartmentsByOrganization(UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        return departmentRepository.findByOrganization(organization)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<DepartmentDTO> getDepartmentByIdAndOrganization(UUID id, UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        return departmentRepository.findById(id)
                .filter(dept -> dept.getOrganization() != null && 
                        dept.getOrganization().getId().equals(organizationId))
                .map(this::convertToDTO);
    }
    
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        Organization organization = organizationRepository.findById(departmentDTO.getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        Department department = new Department();
        department.setName(departmentDTO.getName());
        department.setCode(departmentDTO.getCode());
        department.setOrganization(organization);
        
        Department savedDepartment = departmentRepository.save(department);
        return convertToDTO(savedDepartment);
    }
    
    @Transactional
    public Optional<DepartmentDTO> updateDepartmentForOrganization(UUID id, DepartmentDTO departmentDTO, UUID organizationId) {
        return departmentRepository.findById(id)
                .filter(dept -> dept.getOrganization() != null && 
                        dept.getOrganization().getId().equals(organizationId))
                .map(department -> {
                    department.setName(departmentDTO.getName());
                    department.setCode(departmentDTO.getCode());
                    Department updatedDepartment = departmentRepository.save(department);
                    return convertToDTO(updatedDepartment);
                });
    }
    
    @Transactional
    public boolean deleteDepartmentForOrganization(UUID id, UUID organizationId) {
        Optional<Department> departmentOpt = departmentRepository.findById(id)
                .filter(dept -> dept.getOrganization() != null && 
                        dept.getOrganization().getId().equals(organizationId));
        
        if (departmentOpt.isPresent()) {
            departmentRepository.delete(departmentOpt.get());
            return true;
        }
        
        return false;
    }
    
    private DepartmentDTO convertToDTO(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCode(department.getCode());
        
        if (department.getOrganization() != null) {
            dto.setOrganizationId(department.getOrganization().getId());
            dto.setOrganizationName(department.getOrganization().getName());
        }
        
        return dto;
    }
} 