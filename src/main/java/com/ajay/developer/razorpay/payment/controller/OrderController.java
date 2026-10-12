package com.ajay.developer.razorpay.payment.controller;

import com.ajay.developer.razorpay.merchant.security.MerchantContext;
import com.ajay.developer.razorpay.payment.dto.request.CreateOrderRequest;
import com.ajay.developer.razorpay.payment.dto.responce.OrderResponce;
import com.ajay.developer.razorpay.payment.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<OrderResponce> create(@RequestBody @Valid CreateOrderRequest createOrderRequest){

        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(merchantContext.getMerchantId(),createOrderRequest));
    }

    @GetMapping("/{orderid}")
    public ResponseEntity<OrderResponce> getById(@PathVariable("orderid") UUID orderId){

        return ResponseEntity.ok(orderService.getById(merchantContext.getMerchantId(),orderId));

    }

    @PatchMapping("/{orderid}")
    public ResponseEntity<OrderResponce> cancel(@PathVariable("orderid") UUID orderId){
        return ResponseEntity.ok(orderService.cancel(merchantContext.getMerchantId(),orderId));
    }
}
