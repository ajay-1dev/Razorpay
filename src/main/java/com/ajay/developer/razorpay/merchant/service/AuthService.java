package com.ajay.developer.razorpay.merchant.service;

import com.ajay.developer.razorpay.merchant.dto.request.LoginRequest;
import com.ajay.developer.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ajay.developer.razorpay.merchant.dto.response.LoginResponce;
import com.ajay.developer.razorpay.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
     MerchantResponse signUp(MerchantSignupRequest merchantSignupRequest);

     LoginResponce login(@Valid LoginRequest loginRequest);
}
