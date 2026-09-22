package com.ajay.developer.razorpay.payment.gateway.adapters;

import com.ajay.developer.razorpay.payment.gateway.PaymentAdapter;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentRequest;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CardPaymentAdapter implements PaymentAdapter {
    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {
    return null;
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return null;
    }
}
