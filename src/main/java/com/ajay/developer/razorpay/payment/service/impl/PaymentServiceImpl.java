package com.ajay.developer.razorpay.payment.service.impl;

import com.ajay.developer.razorpay.common.enums.OrderStatus;
import com.ajay.developer.razorpay.common.enums.PaymentEvent;
import com.ajay.developer.razorpay.common.enums.PaymentStatus;
import com.ajay.developer.razorpay.common.exception.BusinessRuleVoilationException;
import com.ajay.developer.razorpay.common.exception.ResourceNotFoundException;
import com.ajay.developer.razorpay.payment.Settlement.PaymentTransitionService;
import com.ajay.developer.razorpay.payment.dto.request.PaymentInitRequest;
import com.ajay.developer.razorpay.payment.dto.responce.PaymentResponce;
import com.ajay.developer.razorpay.payment.entity.OrderRecord;
import com.ajay.developer.razorpay.payment.entity.Payment;
import com.ajay.developer.razorpay.payment.gateway.PaymentGatewayRouter;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentRequest;
import com.ajay.developer.razorpay.payment.gateway.dto.PaymentResult;
import com.ajay.developer.razorpay.payment.mapper.PaymentMapper;
import com.ajay.developer.razorpay.payment.repository.OrderRecordRepository;
import com.ajay.developer.razorpay.payment.repository.PaymentRepository;
import com.ajay.developer.razorpay.payment.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private  final OrderRecordRepository orderRecordRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;
    private final PaymentTransitionService paymentSettlementService;

    @Override
    @Transactional
    public PaymentResponce initiate(UUID merchantId, PaymentInitRequest paymentInitRequest) {
        OrderRecord orderRecord = orderRecordRepository.findByIdAndMerchantId(paymentInitRequest.orderId(), merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Order Record","".valueOf(paymentInitRequest.orderId())));

        if(orderRecord.getOrderStatus() != OrderStatus.CREATED && orderRecord.getOrderStatus() != OrderStatus.ATTEMPTED){
            throw new BusinessRuleVoilationException("Order_cant_be_payable","order is the status of :"+orderRecord.getOrderStatus());
        }

        orderRecord.setOrderStatus(OrderStatus.ATTEMPTED);
        orderRecord.setAttempts(orderRecord.getAttempts() + 1);

        Payment payment = Payment.builder()
                .merchantId(merchantId)
                .order(orderRecord)
                .status(PaymentStatus.CREATED)
                .amount(orderRecord.getAmount())
                .method(paymentInitRequest.method())
                .methodDetails(paymentInitRequest.methodDetails())
                .build();


        payment = paymentRepository.save(payment);
        PaymentRequest paymentRequest = paymentMapper.toPaymentRequest(payment);
        PaymentResult paymentResult = paymentGatewayRouter.initiate(paymentRequest);
        switch (paymentResult) {
            case PaymentResult.Pending pending -> {
                payment.setProcessorReference(pending.registrstionRef());
            }
            case PaymentResult.Failure failure -> {
                //payment.setStatus(PaymentStatus.FAILED);
                paymentSettlementService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDetails(failure.errorDescription());
            }
            case PaymentResult.Sucess sucess -> {

            }
        }
        payment = paymentRepository.save(payment);
        orderRecordRepository.save(orderRecord);
        return paymentMapper.toResponce(payment);
    }

    @Override
    @Transactional
    public PaymentResponce capture(UUID merchantId, UUID paymentId) {

        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId,merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Payment Record","".valueOf(paymentId)));

        //payment.setStatus(PaymentStatus.CAPTURING);
        paymentSettlementService.apply(payment,PaymentEvent.CAPTURE_REQUEST);

        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(),paymentId);

        if(paymentResult instanceof PaymentResult.Sucess sucess){
            //payment.setStatus(PaymentStatus.CAPTURED);
            paymentSettlementService.apply(payment,PaymentEvent.CAPTURE_SUCCESS);
            payment.setCreatedAt(LocalDateTime.now());
            log.info("Payment captured successfully paymentId : {}",paymentId);
        }else if(paymentResult instanceof PaymentResult.Failure failure){
            //payment.setStatus(PaymentStatus.FAILED);
            paymentSettlementService.apply(payment,PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDetails(failure.errorDescription());
            log.warn("Payment failed paymentId : {}",paymentId);
        }
        payment = paymentRepository.save(payment);

        return paymentMapper.toResponce(payment);
    }
}
