package com.ajay.developer.razorpay.vault.dto.responce;

import com.ajay.developer.razorpay.common.entity.CardExpiryCheck;
import com.ajay.developer.razorpay.common.enums.CardBrand;

public record TokenizeResponce(
        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
