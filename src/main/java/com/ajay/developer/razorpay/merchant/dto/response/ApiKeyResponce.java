package com.ajay.developer.razorpay.merchant.dto.response;

import com.ajay.developer.razorpay.common.enums.Environment;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiKeyResponce(
        UUID id,
        String keyId,
        Environment environment,
        boolean enabled,
        LocalDateTime lastUsedAt,
        LocalDateTime createdAt
) {
}
