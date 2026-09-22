package com.ajay.developer.razorpay.payment.processor;

import com.ajay.developer.razorpay.common.enums.PaymentMethod;
import com.ajay.developer.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.ajay.developer.razorpay.payment.processor.dto.responce.PaymentProcessorResponce;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {
    private final Map<PaymentMethod, PaymentProcessor> paymentProcessorMap;
    public PaymentProcessorResponce charge(PaymentProcessorRequest request) {
        PaymentProcessor paymentProcessor = paymentProcessorMap.get(request.method());
        if (paymentProcessor == null) {
            throw new IllegalArgumentException("payment processor not registered with this method : "+request.method());
        }
        return paymentProcessor.charge(request);
    }
}
