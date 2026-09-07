package com.ajay.developer.razorpay.merchant.dto.request;

import com.ajay.developer.razorpay.common.enums.BusinessType;
import jakarta.validation.constraints.*;

public record MerchantSignupRequest(

         @NotNull(message = "Name is required")
         @Size(max = 50,message = "Name should be with in 50 characters")
         String name,
         @Email
         @NotEmpty(message = "Email is required")
         @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|org|net|in|io|ai|co|dev|tech)$")
         String email,
         @NotEmpty
         @Size(min = 8,message = "password length should be greater than or equal to 8")
         String password,
         @Size(max = 50,message = "Bussiness name should be within 50 characters")
         String businessName,
         BusinessType businessType
) {
}
