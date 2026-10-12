package com.ajay.developer.razorpay.common.exception;

import com.ajay.developer.razorpay.common.exception.dto.ErrorResponce;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

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

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<ErrorResponce> handleInvalidStateTransitionException(InvalidStateTransitionException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponce.error(ex.getMessage(), ex.getCurrentState()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponce> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<ErrorResponce.FieldErrors> fieldErrors =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> new ErrorResponce.FieldErrors(
                                error.getField(),
                                error.getDefaultMessage()
                        ))
                        .toList();

        return ResponseEntity.badRequest().body(ErrorResponce.error("VALIDATION_ERROR",
                "Request validation failed",
                fieldErrors));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponce> handleBadCredentialsException(
            BadCredentialsException ex) {

        ErrorResponce response = ErrorResponce.error(
                "INVALID_CREDENTIALS",
                "Invalid email or password"
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponce> handleJwtException(JwtException ex) {

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body( ErrorResponce.error(
                        "INVALID_JWT",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponce> handleRateLimitException(RateLimitException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-RateLimit-Remaning","0")
                .header("Retry-After",String.valueOf(ex.getRetryAfterSeconds()))
                .header("X-RateLimit-Reset",String.valueOf(Instant.now().plusSeconds(ex.getRetryAfterSeconds()).getEpochSecond()))
                .body(ErrorResponce.error("RATE_LIMIT_EXCEEDED", ex.getMessage()));
    }
}
