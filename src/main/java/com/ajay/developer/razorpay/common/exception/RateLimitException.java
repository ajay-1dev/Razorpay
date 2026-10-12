package com.ajay.developer.razorpay.common.exception;

import lombok.Getter;

@Getter
public class RateLimitException extends RuntimeException {
    private int remaining;
    private int retryAfterSeconds;
    public RateLimitException(String message,int retryAfterSeconds) {
        this.retryAfterSeconds = retryAfterSeconds;
        this.remaining = 0;
        super(message);
    }
}
