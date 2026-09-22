package com.ajay.developer.razorpay.payment.gateway.adapters;

import com.ajay.developer.razorpay.common.enums.PaymentMethod;
import com.ajay.developer.razorpay.payment.gateway.PaymentAdapter;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentRequest;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentResult;
import com.ajay.developer.razorpay.payment.processor.PaymentProcessorRouter;
import com.ajay.developer.razorpay.payment.processor.dto.request.PaymentProcessorRequest;
import com.ajay.developer.razorpay.payment.processor.dto.responce.PaymentProcessorResponce;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpiPaymentAdapter implements PaymentAdapter {
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {
        log.info("Initiate payment with UpiPaymentAdapter , paymentId ;{}", paymentRequest.paymentId());
        try {
            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(paymentRequest.paymentId(), PaymentMethod.UPI,
                    paymentRequest.amount(),
                    paymentRequest.methodDetails());

            PaymentProcessorResponce paymentProcessorResponce = paymentProcessorRouter.charge(paymentProcessorRequest);

            return switch (paymentProcessorResponce) {
                case PaymentProcessorResponce.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponce.Pending pending ->
                        new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessorResponce.Success success -> new PaymentResult.Sucess(success.bankReference());

            };
        } catch (Exception ex) {
            log.warn("UPIPAYMENT failed with paymentId {}", paymentRequest.paymentId());
            return new PaymentResult.Failure("UPIPAYMENT_FAILED", ex.getMessage().toUpperCase());
        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Sucess("UPIPAYMENT_SUCCESS");
    }
}
