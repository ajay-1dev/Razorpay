package com.ajay.developer.razorpay.common.exception;

import lombok.Getter;

@Getter
public class BusinessRuleVoilationException extends RuntimeException{
    private final String errorCode;
    public BusinessRuleVoilationException(String errorCode, String message) {
        this.errorCode = errorCode.toUpperCase();
        super(message);
    }

}