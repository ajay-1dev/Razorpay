package com.ajay.developer.razorpay.merchant.dto.request;

import com.ajay.developer.razorpay.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
