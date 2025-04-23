package com.payroll.payroll_system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAdvanceRequestDTO {
    
    @NotNull(message = "Requester ID cannot be null")
    private UUID requestedBy;
    
    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();
    
    @NotEmpty(message = "At least one advance request must be provided")
    @Size(min = 1, max = 500, message = "Number of advance requests must be between 1 and 500")
    private List<@Valid AdvanceRequestDTO> requests;
    
    private String description;
    
    private String callbackUrl;
    
    @Builder.Default
    private boolean skipValidation = false;
} 