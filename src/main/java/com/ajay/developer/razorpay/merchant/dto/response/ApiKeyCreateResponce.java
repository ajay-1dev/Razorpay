package com.ajay.developer.razorpay.merchant.dto.response;

import com.ajay.developer.razorpay.common.enums.Environment;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiKeyCreateResponce(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
