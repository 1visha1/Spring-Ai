package com.ecom.agent.exception;

import com.ecom.agent.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles specific AI-related exceptions, like rate limiting.
     * This prevents noisy stack traces in the logs for expected operational errors.
     */
    @ExceptionHandler(NonTransientAiException.class)
    public ResponseEntity<ApiResponse<Void>> handleAiException(NonTransientAiException ex) {
        // Log a concise warning instead of a full stack trace for rate limit errors
        log.warn("AI service error: {}", ex.getMessage());

        // Check if the error is due to rate limiting
        if (ex.getMessage() != null && ex.getMessage().contains("rate_limit_exceeded")) {
            ApiResponse<Void> response = ApiResponse.error(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    "The service is currently busy. Please try again in a moment."
            );
            return new ResponseEntity<>(response, HttpStatus.TOO_MANY_REQUESTS);
        }

        // Handle other non-transient AI errors with a generic message
        ApiResponse<Void> response = ApiResponse.error(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                "There was an issue with the AI service."
        );
        return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: ", ex);
        ApiResponse<Void> response = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later."
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid request argument: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParams(MissingServletRequestParameterException ex) {
        String name = ex.getParameterName();
        ApiResponse<Void> response = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Missing required parameter: " + name
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String type = ex.getRequiredType().getSimpleName();
        ApiResponse<Void> response = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                String.format("Parameter '%s' should be of type '%s'", name, type)
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}