package com.payroll.payroll_system.repository;

import com.payroll.payroll_system.entity.Department;
import com.payroll.payroll_system.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findByOrganization(Organization organization);
    Optional<Department> findByNameAndOrganization(String name, Organization organization);
    boolean existsByNameAndOrganization(String name, Organization organization);
}