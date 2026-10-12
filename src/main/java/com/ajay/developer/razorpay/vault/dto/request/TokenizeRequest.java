package com.ajay.developer.razorpay.vault.dto.request;

import com.ajay.developer.razorpay.vault.customvalidations.ExpiryCheck;
import com.ajay.developer.razorpay.common.entity.CardExpiryCheck;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.UUID;

public record TokenizeRequest(

        @NotEmpty(message = "pan is required")
        @LuhnCheck
        @Pattern(regexp = "^[0-9]{13,19}$",message = "invalid pan number")
        String pan,

        @NotEmpty(message = "cvv is required")
        @Pattern(regexp = "^[0-9]{3,4}$",message = "cvv length is invalid")
        String cvv,

//        @NotNull(message = "expiry year is required")
//        @ExpiryYear
//        Integer expiryYear,
//
//        @NotNull(message = "expiry month is required")
//        @Min(value = 1 , message = "month should be in between 1 - 12")
//        @Max(value = 12,message = "month should be in between 1 - 12")
//        @ExpiryMonth
//        Integer expiryMonth,

        @ExpiryCheck
        CardExpiryCheck yearAndMonth,

        UUID customerId,

        String cardHolderName
) {
}
