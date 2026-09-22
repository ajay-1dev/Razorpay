package com.ajay.developer.razorpay.payment.gateway.dto;

import com.ajay.developer.razorpay.common.entity.Money;
import com.ajay.developer.razorpay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentRequest(
        UUID paymentId,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentMethod method,
        Map<String,Object> methodDetails
) {
}
