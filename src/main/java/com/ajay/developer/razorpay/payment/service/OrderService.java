package com.ajay.developer.razorpay.payment.service;

import com.ajay.developer.razorpay.payment.dto.request.CreateOrderRequest;
import com.ajay.developer.razorpay.payment.dto.responce.OrderResponce;
import com.ajay.developer.razorpay.payment.dto.responce.PaymentResponce;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponce create(UUID merchantId, CreateOrderRequest createOrderRequest);

    OrderResponce getById(UUID merchantId, UUID orderId);

    OrderResponce cancel(UUID merchantId, UUID orderId);

    List<PaymentResponce> listPayments(UUID merchantId, UUID orderId);
}
