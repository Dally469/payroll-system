package com.payroll.payroll_system.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAdvanceActionDTO {
    
    @NotNull(message = "The user ID performing the action is required")
    private UUID actionBy;
    
    @NotEmpty(message = "At least one advance request ID is required")
    @Size(min = 1, max = 500, message = "Number of advance request IDs must be between 1 and 500")
    private List<UUID> advanceRequestIds;
    
    @NotNull(message = "Action type is required")
    private String action; // APPROVE, REJECT
    
    private String comment;
    
    @Builder.Default
    private boolean notifyEmployees = true;
    
    private String callbackUrl;
} 