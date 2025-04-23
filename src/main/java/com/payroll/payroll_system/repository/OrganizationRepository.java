package com.payroll.payroll_system.repository;

import com.payroll.payroll_system.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findByName(String name);
    boolean existsByName(String name);
    Optional<Organization> findByEmail(String email);
} 