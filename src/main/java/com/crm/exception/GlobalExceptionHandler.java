package com.crm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * Handles exceptions across the entire application
 *
 * @author CRM Team
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.error("Unexpected error occurred", e);

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", "An unexpected error occurred: " + e.getMessage());
        error.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @IllegalArgumentException IllegalArgumentException) {
        log.error("Invalid argument error", e);

        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", e.getMessage());
        error.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
