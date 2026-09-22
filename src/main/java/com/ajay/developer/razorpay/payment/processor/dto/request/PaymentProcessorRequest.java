package com.ajay.developer.razorpay.payment.processor.dto.request;

import com.ajay.developer.razorpay.common.entity.Money;
import com.ajay.developer.razorpay.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(
        UUID processingID,
        UUID paymentID,
        PaymentMethod method,
        Money amount,
        String pan,
        String expiry,
        Map<String,Object> methodDetails

) {
    public static PaymentProcessorRequest card(UUID paymentID,String pan,String expiry,Money amount,Map<String,Object> method){
        return new PaymentProcessorRequest(paymentID,UUID.randomUUID(),PaymentMethod.CARD,amount,pan,expiry,method);
    }

    public static PaymentProcessorRequest nonCard(UUID paymentID,PaymentMethod method,Money amount,Map<String,Object> methodDetails){
        return new PaymentProcessorRequest(paymentID,UUID.randomUUID(),method,amount,null,null,methodDetails);
    }

}
