package com.payroll.payroll_system.repository;

import com.payroll.payroll_system.entity.BatchJob;
import com.payroll.payroll_system.entity.Organization;
import com.payroll.payroll_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BatchJobRepository extends JpaRepository<BatchJob, UUID> {
    List<BatchJob> findByOrganization(Organization organization);
    
    List<BatchJob> findByRequestedBy(User user);
    
    List<BatchJob> findByStatus(String status);
    
    List<BatchJob> findByJobType(String jobType);
    
    List<BatchJob> findByOrganizationAndJobType(Organization organization, String jobType);
    
    List<BatchJob> findByOrganizationAndStatus(Organization organization, String status);
    
    List<BatchJob> findBySubmittedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<BatchJob> findByOrganizationAndSubmittedAtBetween(Organization organization, LocalDateTime start, LocalDateTime end);
    
    Optional<BatchJob> findByIdAndOrganization(UUID id, Organization organization);
} 