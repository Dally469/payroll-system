package com.payroll.payroll_system.controller;

import com.payroll.payroll_system.config.JwtTokenUtil;
import com.payroll.payroll_system.dto.ApiResponse;
import com.payroll.payroll_system.dto.AuthRequest;
import com.payroll.payroll_system.dto.AuthResponse;
import com.payroll.payroll_system.dto.NewUserRequest;
import com.payroll.payroll_system.dto.PasswordChangeRequest;
import com.payroll.payroll_system.dto.PasswordResetRequest;
import com.payroll.payroll_system.dto.PasswordResetTokenRequest;
import com.payroll.payroll_system.entity.User;
import com.payroll.payroll_system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            User user = userService.getCurrentUser(userDetails.getUsername());
            
            String token = jwtTokenUtil.generateToken(userDetails);
            
            AuthResponse authResponse = new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getOrganization() != null ? user.getOrganization().getId() : null,
                    user.getOrganization() != null ? user.getOrganization().getName() : null,
                    user.getRoles(),
                    user.getLastLoginAt()
            );
            
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid username or password"));
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        try {
            User newUser = new User();
            newUser.setUsername(newUserRequest.getUsername());
            newUser.setPassword(newUserRequest.getPassword());
            newUser.setEmail(newUserRequest.getEmail());
            newUser.setFirstName(newUserRequest.getFirstName());
            newUser.setLastName(newUserRequest.getLastName());
            newUser.setRoles(newUserRequest.getRoles());
            
            User createdUser = userService.createUser(newUser, newUserRequest.getOrganizationId());
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("userId", createdUser.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(responseData, "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@Valid @RequestBody PasswordResetTokenRequest request) {
        try {
            userService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok(ApiResponse.success(null, "Password reset email sent"));
        } catch (Exception e) {
            log.error("Error requesting password reset", e);
            // Don't expose whether the email exists or not for security reasons
            return ResponseEntity.ok(ApiResponse.success(null, "If your email is registered, you will receive a password reset link"));
        }
    }

    @PostMapping("/reset-password/confirm")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success(null, "Password reset successful"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequest request) {
        try {
            userService.changePassword(
                    userDetails.getUsername(),
                    request.getCurrentPassword(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Current password is incorrect"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/users/organization/{organizationId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUsersByOrganization(
            @PathVariable UUID organizationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User currentUser = userService.getCurrentUser(userDetails.getUsername());
        
        // Check if the user belongs to the requested organization or has admin role
        if (currentUser.getOrganization() == null || 
            !currentUser.getOrganization().getId().equals(organizationId) &&
            !currentUser.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Access denied to organization users"));
        }
        
        List<User> users = userService.getUsersByOrganization(organizationId);
        
        List<Map<String, Object>> userResponses = users.stream()
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", user.getId());
                    response.put("username", user.getUsername());
                    response.put("email", user.getEmail());
                    response.put("firstName", user.getFirstName());
                    response.put("lastName", user.getLastName());
                    response.put("roles", user.getRoles());
                    response.put("enabled", user.isEnabled());
                    return response;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(userResponses, "Users retrieved successfully"));
    }
} 