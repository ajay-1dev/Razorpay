package com.ajay.developer.razorpay.common.ratelimit;

public record RateLimitResult(
        boolean isAllowed,
        int remaning,
        int retryAfterSeconds
) {

    public static RateLimitResult allowed(int remaning){
        return new RateLimitResult(true,remaning,0);
    }

    public static RateLimitResult denied(int retryAfterSeconds){
        return new RateLimitResult(false,0,retryAfterSeconds);
    }
}
