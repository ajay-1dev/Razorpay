package com.ajay.developer.razorpay.merchant.cache;

import com.ajay.developer.razorpay.common.enums.Environment;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyCacheEntry(
        String keyId,
        String keySecretHash,
        String previousKeySecretHash,
        LocalDateTime gracePeriodExpiredAt,
        UUID merchantId,
        Environment environment,
        boolean enabled
) {
    public boolean isInGracePeriod() {
        return gracePeriodExpiredAt != null && gracePeriodExpiredAt.isBefore(LocalDateTime.now());
    }
}
