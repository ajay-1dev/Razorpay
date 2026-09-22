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
public class NetbankingPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponce charge(PaymentProcessorRequest request) {

        final String BANK_CODE_FAIL = "BANK_CODE_FAIL";
        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("BANK").toString(): null;

        if(BANK_CODE_FAIL.equals(bankCode)){
            return new PaymentProcessorResponce.Failure("BANK_CODE_FAIL", BANK_CODE_FAIL);
        }

        String processorRef = "NBK_PROCESSOR"+ RandomizerUtil.randomBase64(16);

        String redirectRef = "http://randomlink.com"+processorRef;

        return new PaymentProcessorResponce.Success(processorRef, redirectRef);
    }
}
