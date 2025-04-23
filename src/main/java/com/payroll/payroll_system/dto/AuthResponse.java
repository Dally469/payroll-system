package com.payroll.payroll_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UUID organizationId;
    private String organizationName;
    private Set<String> roles;
    private LocalDateTime lastLoginAt;
} 