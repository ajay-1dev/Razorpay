package com.ajay.developer.razorpay.payment.gateway;

import com.ajay.developer.razorpay.common.enums.PaymentMethod;
import com.ajay.developer.razorpay.payment.config.PaymentAdapterConfig;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentRequest;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {
    private Map<PaymentMethod,PaymentAdapter> paymentAdapterConfig;
    public PaymentResult initiate(PaymentRequest paymentRequest) {

        PaymentAdapter paymentAdapter = paymentAdapterConfig.get(paymentRequest.method());
        if(paymentAdapter == null) {
            throw new IllegalArgumentException("no payment adapter found for method " + paymentRequest.method()+"".toUpperCase());
        }
        return paymentAdapter.initiate(paymentRequest);
    }

    public PaymentResult capture(PaymentMethod paymentMethod,UUID paymentId) {
        PaymentAdapter paymentAdapter = paymentAdapterConfig.get(paymentMethod);
        if(paymentAdapter == null) {
            throw new IllegalArgumentException("no payment adapter found for method " + paymentMethod+"".toUpperCase());
        }
        return paymentAdapter.capture(paymentId);
    }
}
