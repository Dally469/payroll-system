package com.payroll.payroll_system.repository;

import com.payroll.payroll_system.entity.Department;
import com.payroll.payroll_system.entity.Employee;
import com.payroll.payroll_system.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    List<Employee> findByDepartmentId(UUID departmentId);

    @Query("SELECT e FROM Employee e WHERE e.phone = :phone")
    Employee findByPhone(@Param("phone") String phone);

    List<Employee> findByDepartment(Department department);
    List<Employee> findByOrganization(Organization organization);
    List<Employee> findByDepartmentAndOrganization(Department department, Organization organization);
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByDocumentId(String documentId);
    boolean existsByEmail(String email);
    boolean existsByDocumentId(String documentId);
    Optional<Employee> findByIdAndOrganizationId(UUID id, UUID organizationId);
}