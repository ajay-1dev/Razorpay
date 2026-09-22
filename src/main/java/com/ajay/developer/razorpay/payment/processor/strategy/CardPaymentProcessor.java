package com.ajay.developer.razorpay.payment.processor.strategy;

import com.ajay.developer.razorpay.payment.processor.PaymentProcessor;
import com.ajay.developer.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.ajay.developer.razorpay.payment.processor.dto.responce.PaymentProcessorResponce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponce charge(PaymentProcessorRequest request) {
        return null;
    }
}
