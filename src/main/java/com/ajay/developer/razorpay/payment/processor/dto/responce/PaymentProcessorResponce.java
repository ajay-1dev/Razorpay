package com.ajay.developer.razorpay.payment.processor.dto.responce;

public sealed interface PaymentProcessorResponce permits
        PaymentProcessorResponce.Pending,
        PaymentProcessorResponce.Success,
        PaymentProcessorResponce.Failure {

    record Pending(String processorReference) implements   PaymentProcessorResponce{}
    record Success(String processorReference, String bankReference) implements  PaymentProcessorResponce{}
    record Failure(String errorCode, String errorDescription)   implements   PaymentProcessorResponce{}

}
