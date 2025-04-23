package com.payroll.payroll_system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private String token;
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status("success")
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, String message, String token) {
        return ApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .token(token)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder()
                .status("error")
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ApiResponse<T> fromHttpStatus(HttpStatus httpStatus, String message) {
        String status = httpStatus.is2xxSuccessful() ? "success" : "error";
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .build();
    }
} 