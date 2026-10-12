package com.ajay.developer.razorpay.payment.controller;

import com.ajay.developer.razorpay.merchant.security.MerchantContext;
import com.ajay.developer.razorpay.payment.dto.request.PaymentInitRequest;
import com.ajay.developer.razorpay.payment.dto.responce.PaymentResponce;
import com.ajay.developer.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<PaymentResponce> initiate(@RequestBody @Valid PaymentInitRequest paymentInitRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiate(merchantContext.getMerchantId(), paymentInitRequest));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponce> capture(@PathVariable("paymentId") UUID paymentId) {
        return ResponseEntity.ok(paymentService.capture(merchantContext.getMerchantId(),paymentId));
    }
}
