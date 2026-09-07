package com.ajay.developer.razorpay.merchant.controller;

import com.ajay.developer.razorpay.merchant.dto.response.MerchantResponse;
import com.ajay.developer.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ajay.developer.razorpay.merchant.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MerchantResponse> signUp(@RequestBody  @Valid MerchantSignupRequest merchantSignupRequest) {
        MerchantResponse merchantResponse = authService.signUp(merchantSignupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(merchantResponse);
    }
}
