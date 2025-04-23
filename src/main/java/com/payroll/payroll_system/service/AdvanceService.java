package com.payroll.payroll_system.service;

import com.payroll.payroll_system.dto.AdvanceDTO;
import com.payroll.payroll_system.dto.AdvanceRequestDTO;
import com.payroll.payroll_system.entity.Advance;
import com.payroll.payroll_system.entity.Employee;
import com.payroll.payroll_system.entity.Organization;
import com.payroll.payroll_system.entity.User;
import com.payroll.payroll_system.repository.AdvanceRepository;
import com.payroll.payroll_system.repository.EmployeeRepository;
import com.payroll.payroll_system.repository.OrganizationRepository;
import com.payroll.payroll_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdvanceService {
    @Autowired
    private AdvanceRepository advanceRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<AdvanceDTO> getAdvancesByOrganization(UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        return advanceRepository.findByOrganization(organization)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AdvanceDTO> getAdvancesByOrganizationAndStatus(UUID organizationId, String status) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        return advanceRepository.findByOrganizationAndStatus(organization, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AdvanceDTO> getAdvancesByEmployeeAndOrganization(UUID employeeId, UUID organizationId) {
        Employee employee = employeeRepository.findByIdAndOrganizationId(employeeId, organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        
        return advanceRepository.findByEmployee(employee)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AdvanceDTO> getAdvancesByEmployeeAndStatusAndOrganization(UUID employeeId, String status, UUID organizationId) {
        Employee employee = employeeRepository.findByIdAndOrganizationId(employeeId, organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        
        return advanceRepository.findByEmployeeAndStatus(employee, status)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<AdvanceDTO> getAdvanceByIdAndOrganization(UUID advanceId, UUID organizationId) {
        return advanceRepository.findById(advanceId)
                .filter(adv -> adv.getOrganization() != null && 
                        adv.getOrganization().getId().equals(organizationId))
                .map(this::convertToDTO);
    }
    
    @Transactional
    public AdvanceDTO requestAdvance(AdvanceRequestDTO requestDTO, UUID organizationId) {
        Employee employee = employeeRepository.findByIdAndOrganizationId(requestDTO.getEmployeeId(), organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        Advance advance = new Advance();
        advance.setEmployee(employee);
        advance.setOrganization(organization);
        advance.setAmount(requestDTO.getAmount());
        advance.setReason(requestDTO.getReason());
        advance.setRequestDate(requestDTO.getRequestDate());
        advance.setRepaymentDate(requestDTO.getRepaymentDate());
        advance.setStatus("PENDING");
        
        Advance savedAdvance = advanceRepository.save(advance);
        return convertToDTO(savedAdvance);
    }
    
    @Transactional
    public Optional<AdvanceDTO> approveAdvance(UUID advanceId, UUID approverId, UUID organizationId) {
        Optional<Advance> advanceOpt = advanceRepository.findById(advanceId)
                .filter(adv -> adv.getOrganization() != null && 
                        adv.getOrganization().getId().equals(organizationId));
        
        if (advanceOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));
        
        Advance advance = advanceOpt.get();
        advance.setStatus("APPROVED");
        advance.setApprovedBy(approver);
        advance.setApprovalDate(LocalDateTime.now());
        
        Advance updatedAdvance = advanceRepository.save(advance);
        return Optional.of(convertToDTO(updatedAdvance));
    }
    
    @Transactional
    public Optional<AdvanceDTO> rejectAdvance(UUID advanceId, String reason, UUID organizationId) {
        return advanceRepository.findById(advanceId)
                .filter(adv -> adv.getOrganization() != null && 
                        adv.getOrganization().getId().equals(organizationId))
                .map(advance -> {
                    advance.setStatus("REJECTED");
                    advance.setRejectionReason(reason);
                    Advance updatedAdvance = advanceRepository.save(advance);
                    return convertToDTO(updatedAdvance);
                });
    }
    
    @Transactional
    public Optional<AdvanceDTO> recordRepayment(UUID advanceId, double amount, UUID organizationId) {
        return advanceRepository.findById(advanceId)
                .filter(adv -> adv.getOrganization() != null && 
                        adv.getOrganization().getId().equals(organizationId)
                        && "APPROVED".equals(adv.getStatus()))
                .map(advance -> {
                    BigDecimal repaidAmount = advance.getRepaidAmount().add(new BigDecimal(amount));
                    advance.setRepaidAmount(repaidAmount);
                    
                    // Check if fully repaid
                    if (repaidAmount.compareTo(advance.getAmount()) >= 0) {
                        advance.setFullyRepaid(true);
                    }
                    
                    Advance updatedAdvance = advanceRepository.save(advance);
                    return convertToDTO(updatedAdvance);
                });
    }
    
    private AdvanceDTO convertToDTO(Advance advance) {
        AdvanceDTO dto = new AdvanceDTO();
        dto.setId(advance.getId());
        
        if (advance.getEmployee() != null) {
            dto.setEmployeeId(advance.getEmployee().getId());
            dto.setEmployeeName(advance.getEmployee().getFirstName() + " " + advance.getEmployee().getLastName());
        }
        
        if (advance.getOrganization() != null) {
            dto.setOrganizationId(advance.getOrganization().getId());
            dto.setOrganizationName(advance.getOrganization().getName());
        }
        
        dto.setAmount(advance.getAmount());
        dto.setReason(advance.getReason());
        dto.setStatus(advance.getStatus());
        dto.setRejectionReason(advance.getRejectionReason());
        
        if (advance.getApprovedBy() != null) {
            dto.setApprovedById(advance.getApprovedBy().getId());
            dto.setApprovedByName(advance.getApprovedBy().getFirstName() + " " + advance.getApprovedBy().getLastName());
        }
        
        dto.setApprovalDate(advance.getApprovalDate());
        dto.setRequestDate(advance.getRequestDate());
        dto.setRepaymentDate(advance.getRepaymentDate());
        dto.setFullyRepaid(advance.isFullyRepaid());
        dto.setRepaidAmount(advance.getRepaidAmount());
        
        // Calculate remaining amount
        if (advance.getAmount() != null && advance.getRepaidAmount() != null) {
            dto.setRemainingAmount(advance.getAmount().subtract(advance.getRepaidAmount()));
        }
        
        return dto;
    }
} 