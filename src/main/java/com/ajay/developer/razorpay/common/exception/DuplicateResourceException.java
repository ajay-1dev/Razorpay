package com.ajay.developer.razorpay.common.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException{
    private final String errorCode;
    public DuplicateResourceException(String errorCode,String message) {
        this.errorCode = errorCode;
        super(message);
    }
}
