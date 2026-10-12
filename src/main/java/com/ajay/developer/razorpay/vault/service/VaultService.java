package com.ajay.developer.razorpay.vault.service;


import com.ajay.developer.razorpay.common.entity.Money;
import com.ajay.developer.razorpay.common.enums.CardBrand;
import com.ajay.developer.razorpay.payment.processor.dto.responce.PaymentProcessorResponce;
import com.ajay.developer.razorpay.vault.dto.request.TokenizeRequest;
import com.ajay.developer.razorpay.vault.dto.responce.TokenizeResponce;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

public interface VaultService {
    TokenizeResponce tokenize(UUID merchantId, @Valid TokenizeRequest tokenizeRequest);
    CardBrand detectBrand(String pan);

    PaymentProcessorResponce charge(UUID paymentId,String token, Money amount, Map<String, Object> stringObjectMap);
}
