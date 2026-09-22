package com.ajay.developer.razorpay.payment.gateway.dto;

public sealed interface PaymentResult permits PaymentResult.Pending,PaymentResult.Failure,PaymentResult.Sucess {

    record Pending(String registrstionRef) implements PaymentResult {}
    record Failure(String errorCode,String errorDescription) implements PaymentResult {}
    record Sucess(String bankReference) implements PaymentResult {}
}
