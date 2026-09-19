package com.ajay.developer.razorpay.payment.dto.responce;

import com.ajay.developer.razorpay.common.entity.Money;
import com.ajay.developer.razorpay.common.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record OrderResponce(
        UUID id,
        UUID merchantId,
        String receipt,
        Money money,
        OrderStatus status,
        Integer attempts,
        Map<String,Object> notes,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
}
