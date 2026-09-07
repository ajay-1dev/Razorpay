package com.ajay.developer.razorpay.common.exception.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponce(
        String errorCode,
        String errorMessage,
        LocalDateTime timestamp,
        List<FieldErrors> fieldErrors) {
    public record FieldErrors(String field, String message) {}
    public static ErrorResponce error(String errorCode, String errorMessage) {
        return new ErrorResponce(errorCode,errorMessage,LocalDateTime.now(),null);
    }

    public static ErrorResponce error(String errorCode, String errorMessage,List<FieldErrors> fieldErrors) {
        return new ErrorResponce(errorCode,errorMessage,LocalDateTime.now(),fieldErrors);
    }
}
