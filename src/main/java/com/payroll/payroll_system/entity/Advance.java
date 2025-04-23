package com.payroll.payroll_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "advances")
public class Advance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(nullable = false)
    private String reason;
    
    private String status; // PENDING, APPROVED, REJECTED
    
    private String rejectionReason;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    private LocalDateTime approvalDate;
    
    @Column(nullable = false)
    private LocalDate requestDate;
    
    @Column(nullable = false)
    private LocalDate repaymentDate;
    
    private boolean fullyRepaid;
    
    private BigDecimal repaidAmount;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
        if (this.repaidAmount == null) {
            this.repaidAmount = BigDecimal.ZERO;
        }
        this.fullyRepaid = false;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
} 