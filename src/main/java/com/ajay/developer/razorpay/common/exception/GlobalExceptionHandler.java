package com.ajay.developer.razorpay.common.exception;

import com.ajay.developer.razorpay.common.exception.dto.ErrorResponce;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponce> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponce.error(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(BusinessRuleVoilationException.class)
    public ResponseEntity<ErrorResponce> handleResourceNotFoundException(BusinessRuleVoilationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponce.error(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponce> handleDuplicateResourceException(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponce.error(ex.getErrorCode(), ex.getMessage()));
    }
}
