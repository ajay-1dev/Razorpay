package com.ajay.developer.razorpay.common.ratelimit;

public interface RateLimiter {

    RateLimitResult check(String key,int maxAllowedRequests,long windowSeconds);
}
