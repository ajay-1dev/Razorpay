package com.ajay.developer.razorpay.payment.dto.request;

import com.ajay.developer.razorpay.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record PaymentInitRequest(

        @NotNull(message = "please provide the order_id")
        UUID orderId,
        @NotNull(message = "please select the payment method")
        PaymentMethod method,
        Map<String,Object> methodDetails
) {
}
