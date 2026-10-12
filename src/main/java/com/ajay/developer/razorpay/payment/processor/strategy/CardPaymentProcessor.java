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
public class CardPaymentProcessor implements PaymentProcessor {
    private final String PAN_CARD_DECLINED = "4000000000000002";
    private final String PAN_CARD_EXPIRED = "4000000000000069";
    @Override
    public PaymentProcessorResponce charge(PaymentProcessorRequest request) {

        String pan = request.pan();

        if(PAN_CARD_EXPIRED.equals(pan)){
            log.warn("pan card expired:************{}", pan.substring(pan.length()-4));
            return new PaymentProcessorResponce.Failure("CARD_EXPIRED","pan card expired");
        }

        if(PAN_CARD_DECLINED.equals(pan)){
            log.warn("pan card declined:************{}", pan.substring(pan.length()-4));
            return new PaymentProcessorResponce.Failure("CARD_DECLINED","pan card declined");
        }
        String processorRef = "CARD_PROCESSOR"+ RandomizerUtil.randomBase64(16);
        return new PaymentProcessorResponce.Pending(processorRef);}
}
