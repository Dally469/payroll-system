package com.payroll.payroll_system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payroll.payroll_system.dto.BatchJobResponseDTO;
import com.payroll.payroll_system.dto.BatchPayrollRequestDTO;
import com.payroll.payroll_system.dto.BatchAdvanceRequestDTO;
import com.payroll.payroll_system.entity.BatchJob;
import com.payroll.payroll_system.entity.Organization;
import com.payroll.payroll_system.entity.User;
import com.payroll.payroll_system.repository.BatchJobRepository;
import com.payroll.payroll_system.repository.OrganizationRepository;
import com.payroll.payroll_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class BatchJobService {
    
    private static final Logger logger = LoggerFactory.getLogger(BatchJobService.class);
    
    @Autowired
    private PayrollService payrollService;
    
    @Autowired
    private AdvanceService advanceService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;
    
    @Autowired
    private BatchJobRepository batchJobRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional(readOnly = true)
    public List<BatchJobResponseDTO> getBatchJobsByOrganization(UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        return batchJobRepository.findByOrganization(organization).stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    @Transactional(readOnly = true)
    public Optional<BatchJobResponseDTO> getBatchJobByIdAndOrganization(UUID jobId, UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        return batchJobRepository.findByIdAndOrganization(jobId, organization)
                .map(this::convertToDTO);
    }
    
    @Async
    @Transactional
    public CompletableFuture<BatchJobResponseDTO> processBatchPayroll(BatchPayrollRequestDTO batchRequest, UUID organizationId) {
        logger.info("Starting batch payroll processing for organization: {}", organizationId);
        
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        User requestedBy = userRepository.findById(batchRequest.getRequestedBy())
                .orElseThrow(() -> new IllegalArgumentException("Requesting user not found"));
        
        // Create and save initial job record
        BatchJob batchJob = new BatchJob();
        batchJob.setJobType("PAYROLL");
        batchJob.setStatus("PROCESSING");
        batchJob.setSubmittedAt(LocalDateTime.now());
        batchJob.setStartedAt(LocalDateTime.now());
        batchJob.setTotalRequests(batchRequest.getPayrolls().size());
        batchJob.setProcessedRequests(0);
        batchJob.setSuccessfulRequests(0);
        batchJob.setFailedRequests(0);
        batchJob.setCallbackUrl(batchRequest.getCallbackUrl());
        batchJob.setDescription(batchRequest.getDescription());
        batchJob.setRequestedBy(requestedBy);
        batchJob.setOrganization(organization);
        
        batchJob = batchJobRepository.save(batchJob);
        
        try {
            // Process each payroll request
            for (var payrollRequest : batchRequest.getPayrolls()) {
                try {
                    payrollService.generatePayroll(
                            payrollRequest.getEmployeeId(),
                            payrollRequest.getStartDate(),
                            payrollRequest.getEndDate(),
                            organizationId);
                    
                    batchJob.setProcessedRequests(batchJob.getProcessedRequests() + 1);
                    batchJob.setSuccessfulRequests(batchJob.getSuccessfulRequests() + 1);
                    batchJobRepository.save(batchJob);
                } catch (Exception e) {
                    logger.error("Error processing payroll for employee {}: {}", 
                            payrollRequest.getEmployeeId(), e.getMessage());
                    batchJob.setProcessedRequests(batchJob.getProcessedRequests() + 1);
                    batchJob.setFailedRequests(batchJob.getFailedRequests() + 1);
                    batchJobRepository.save(batchJob);
                }
            }
            
            batchJob.setStatus("COMPLETED");
            batchJob.setCompletedAt(LocalDateTime.now());
            batchJobRepository.save(batchJob);
        } catch (Exception e) {
            logger.error("Error in batch payroll processing: {}", e.getMessage());
            batchJob.setStatus("FAILED");
            batchJob.setCompletedAt(LocalDateTime.now());
            batchJob.setResultDetails("Error: " + e.getMessage());
            batchJobRepository.save(batchJob);
        }
        
        return CompletableFuture.completedFuture(convertToDTO(batchJob));
    }
    
    @Async
    @Transactional
    public CompletableFuture<BatchJobResponseDTO> processBatchAdvances(BatchAdvanceRequestDTO batchRequest, UUID organizationId) {
        logger.info("Starting batch advance processing for organization: {}", organizationId);
        
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found"));
        
        User requestedBy = userRepository.findById(batchRequest.getRequestedBy())
                .orElseThrow(() -> new IllegalArgumentException("Requesting user not found"));
        
        // Create and save initial job record
        BatchJob batchJob = new BatchJob();
        batchJob.setJobType("ADVANCE");
        batchJob.setStatus("PROCESSING");
        batchJob.setSubmittedAt(LocalDateTime.now());
        batchJob.setStartedAt(LocalDateTime.now());
        batchJob.setTotalRequests(batchRequest.getRequests().size());
        batchJob.setProcessedRequests(0);
        batchJob.setSuccessfulRequests(0);
        batchJob.setFailedRequests(0);
        batchJob.setCallbackUrl(batchRequest.getCallbackUrl());
        batchJob.setDescription(batchRequest.getDescription());
        batchJob.setRequestedBy(requestedBy);
        batchJob.setOrganization(organization);
        
        batchJob = batchJobRepository.save(batchJob);
        
        try {
            // Process each advance request
            for (var advanceRequest : batchRequest.getRequests()) {
                try {
                    advanceService.requestAdvance(advanceRequest, organizationId);
                    
                    batchJob.setProcessedRequests(batchJob.getProcessedRequests() + 1);
                    batchJob.setSuccessfulRequests(batchJob.getSuccessfulRequests() + 1);
                    batchJobRepository.save(batchJob);
                } catch (Exception e) {
                    logger.error("Error processing advance for employee {}: {}", 
                            advanceRequest.getEmployeeId(), e.getMessage());
                    batchJob.setProcessedRequests(batchJob.getProcessedRequests() + 1);
                    batchJob.setFailedRequests(batchJob.getFailedRequests() + 1);
                    batchJobRepository.save(batchJob);
                }
            }
            
            batchJob.setStatus("COMPLETED");
            batchJob.setCompletedAt(LocalDateTime.now());
            batchJobRepository.save(batchJob);
        } catch (Exception e) {
            logger.error("Error in batch advance processing: {}", e.getMessage());
            batchJob.setStatus("FAILED");
            batchJob.setCompletedAt(LocalDateTime.now());
            batchJob.setResultDetails("Error: " + e.getMessage());
            batchJobRepository.save(batchJob);
        }
        
        return CompletableFuture.completedFuture(convertToDTO(batchJob));
    }
    
    private BatchJobResponseDTO convertToDTO(BatchJob batchJob) {
        return BatchJobResponseDTO.builder()
                .batchJobId(batchJob.getId())
                .jobType(batchJob.getJobType())
                .status(batchJob.getStatus())
                .requestedBy(batchJob.getRequestedBy() != null ? batchJob.getRequestedBy().getId() : null)
                .submittedAt(batchJob.getSubmittedAt())
                .startedAt(batchJob.getStartedAt())
                .completedAt(batchJob.getCompletedAt())
                .totalRequests(batchJob.getTotalRequests())
                .processedRequests(batchJob.getProcessedRequests())
                .successfulRequests(batchJob.getSuccessfulRequests())
                .failedRequests(batchJob.getFailedRequests())
                .callbackUrl(batchJob.getCallbackUrl())
                .description(batchJob.getDescription())
                .build();
    }
} 