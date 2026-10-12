package com.ajay.developer.razorpay.payment.service;

import com.ajay.developer.razorpay.payment.dto.request.PaymentInitRequest;
import com.ajay.developer.razorpay.payment.dto.responce.PaymentResponce;

import java.util.UUID;

public interface PaymentService {

    PaymentResponce initiate(UUID merchantId, PaymentInitRequest paymentInitRequest);


    PaymentResponce capture(UUID merchantId, UUID paymentId);

    void resolveAuthorizing(UUID PaymentId, boolean approve, String bankRef, String errorCode, String errorDescription);
}
