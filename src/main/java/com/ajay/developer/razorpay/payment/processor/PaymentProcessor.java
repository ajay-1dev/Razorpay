package com.ajay.developer.razorpay.payment.processor;

import com.ajay.developer.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.ajay.developer.razorpay.payment.processor.dto.responce.PaymentProcessorResponce;

public interface PaymentProcessor {
    PaymentProcessorResponce charge(PaymentProcessorRequest request);
}
