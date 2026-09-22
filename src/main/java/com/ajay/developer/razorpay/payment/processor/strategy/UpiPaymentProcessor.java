package com.ajay.developer.razorpay.payment.processor.strategy;

import com.ajay.developer.razorpay.common.util.RandomizerUtil;
import com.ajay.developer.razorpay.payment.processor.PaymentProcessor;
import com.ajay.developer.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.ajay.developer.razorpay.payment.processor.dto.responce.PaymentProcessorResponce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponce charge(PaymentProcessorRequest request) {
        final String VPA_CODE_FAIL = "fail@axis";
        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("VPA").toString(): null;

        if(VPA_CODE_FAIL.equals(bankCode)){
            return new PaymentProcessorResponce.Failure("VPA_CODE_FAIL", VPA_CODE_FAIL);
        }

        String processorRef = "UPI_PROCESSOR"+ RandomizerUtil.randomBase64(16);

        String bankRef = "BANK_REF"+ RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponce.Success(processorRef, bankRef);
    }}
