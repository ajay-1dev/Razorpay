package com.ajay.developer.razorpay.payment.service.impl;

import com.ajay.developer.razorpay.common.enums.OrderStatus;
import com.ajay.developer.razorpay.common.exception.BusinessRuleVoilationException;
import com.ajay.developer.razorpay.common.exception.DuplicateResourceException;
import com.ajay.developer.razorpay.common.exception.ResourceNotFoundException;
import com.ajay.developer.razorpay.payment.dto.request.CreateOrderRequest;
import com.ajay.developer.razorpay.payment.dto.responce.OrderResponce;
import com.ajay.developer.razorpay.payment.dto.responce.PaymentResponce;
import com.ajay.developer.razorpay.payment.entity.OrderRecord;
import com.ajay.developer.razorpay.payment.entity.Payment;
import com.ajay.developer.razorpay.payment.mapper.OrderMapper;
import com.ajay.developer.razorpay.payment.mapper.PaymentMapper;
import com.ajay.developer.razorpay.payment.repository.OrderRecordRepository;
import com.ajay.developer.razorpay.payment.repository.PaymentRepository;
import com.ajay.developer.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRecordRepository orderRecordRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${orderexpiryminutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    public OrderResponce create(UUID merchantId, CreateOrderRequest createOrderRequest) {
        if(createOrderRequest.receipt() != null && orderRecordRepository.existsByMerchantIdAndReceipt(merchantId,createOrderRequest.receipt())){
            throw new DuplicateResourceException("ORDER_RECEIVED_DUPLICATE","Order with receipt already exists: "+createOrderRequest.receipt());
        }
        OrderRecord orderRecord = OrderRecord.builder()
                .merchantId(merchantId)
                .receipt(createOrderRequest.receipt())
                .notes(createOrderRequest.notes())
                .amount(createOrderRequest.amount())
                .orderStatus(OrderStatus.CREATED)
                .expiresAt(createOrderRequest.expiresAt() != null ? createOrderRequest.expiresAt(): LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes))
                .build();
        orderRecord = orderRecordRepository.save(orderRecord);

    return new OrderResponce(orderRecord.getId()
            ,orderRecord.getMerchantId(),
            orderRecord.getReceipt(),
            orderRecord.getAmount(),
            orderRecord.getOrderStatus(),
            orderRecord.getAttempts(),
            orderRecord.getNotes(),
            orderRecord.getExpiresAt(),
            null);
    }

    @Override
    public OrderResponce getById(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRecordRepository.findByIdAndMerchantId(orderId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER-NOT EXITS WITH",orderId.toString()));
//        return new OrderResponce(orderRecord.getId()
//                ,orderRecord.getMerchantId(),
//                orderRecord.getReceipt(),
//                orderRecord.getAmount(),
//                orderRecord.getOrderStatus(),
//                orderRecord.getAttempts(),
//                orderRecord.getNotes(),
//                orderRecord.getExpiresAt(),
//                null);
        return orderMapper.orderToOrderResponce(orderRecord);

    }

    @Override
    @Transactional
    public OrderResponce cancel(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRecordRepository.findByIdAndMerchantId(orderId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER-NOT EXITS WITH",orderId.toString()));
        if(orderRecord.getOrderStatus() == OrderStatus.CANCELED ||  orderRecord.getOrderStatus() == OrderStatus.PAID){
            throw new BusinessRuleVoilationException("ORDER_CANNOT_CANCELED",orderId.toString());
        }
        orderRecord.setOrderStatus(OrderStatus.CANCELED);
        orderRecord = orderRecordRepository.save(orderRecord);
//        return new OrderResponce(orderRecord.getId()
//                ,orderRecord.getMerchantId(),
//                orderRecord.getReceipt(),
//                orderRecord.getAmount(),
//                orderRecord.getOrderStatus(),
//                orderRecord.getAttempts(),
//                orderRecord.getNotes(),
//                orderRecord.getExpiresAt(),
//                null);
        return orderMapper.orderToOrderResponce(orderRecord);
    }

    @Override
    public List<PaymentResponce> listPayments(UUID merchantId, UUID orderId) {
        OrderRecord orderRecord = orderRecordRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER-NOT EXITS WITH", orderId.toString()));

        List<Payment> payments = paymentRepository.findByOrder_Id(orderId);
//        return payments.stream()
//                .map(payment -> new PaymentResponce(payment.getId(),payment.getOrderRecord().getId(),payment.getMerchantId(),payment.getAmount(),payment.getStatus(),payment.getMethod(),payment.getMethodDetails(),)).toList();
//
//        return payments.stream()
//                .map(paymentMapper::toResponceList)
//                .toList();

        return paymentMapper.toResponceList(payments);
    }
}
