package com.ajay.developer.razorpay.payment.gateway.adapters;

import com.ajay.developer.razorpay.payment.gateway.PaymentAdapter;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentRequest;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentResult;
import com.ajay.developer.razorpay.payment.processor.dto.responce.PaymentProcessorResponce;
import com.ajay.developer.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultService vaultService;
    @Override
    public PaymentResult initiate(PaymentRequest paymentRequest) {

        String token = (String) paymentRequest.methodDetails().get("token");
        PaymentProcessorResponce paymentProcessorResponce =
                vaultService.charge(paymentRequest.paymentId()
                        ,token
                        ,paymentRequest.amount()
                        ,paymentRequest.methodDetails());
        return switch (paymentProcessorResponce){
            case PaymentProcessorResponce.Success success -> new PaymentResult.Sucess(success.bankReference());
            case PaymentProcessorResponce.Failure failure -> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponce.Pending pending -> new PaymentResult.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return null;
    }
}
