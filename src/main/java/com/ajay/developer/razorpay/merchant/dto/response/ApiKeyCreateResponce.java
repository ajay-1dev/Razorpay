package com.ajay.developer.razorpay.merchant.dto.response;

import com.ajay.developer.razorpay.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponce(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
