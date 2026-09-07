package com.ajay.developer.razorpay.merchant.dto.response;

import com.ajay.developer.razorpay.common.enums.BusinessType;
import com.ajay.developer.razorpay.common.enums.MerchantStatus;
import lombok.NoArgsConstructor;

import java.util.UUID;


public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus

) {
}
