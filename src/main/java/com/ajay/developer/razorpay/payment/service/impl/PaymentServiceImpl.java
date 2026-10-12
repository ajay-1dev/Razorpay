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
    private final PaymentTransitionService paymentTransitionService;

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
                .ideopotencyKey(UUID.randomUUID().toString())
                .methodDetails(paymentInitRequest.methodDetails())
                .build();


        payment = paymentRepository.save(payment);
        PaymentRequest paymentRequest = paymentMapper.toPaymentRequest(payment);
        paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult paymentResult = paymentGatewayRouter.initiate(paymentRequest);
        switch (paymentResult) {
            case PaymentResult.Pending pending -> {
                payment.setProcessorReference(pending.registrstionRef());
            }
            case PaymentResult.Failure failure -> {
                //payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDetails(failure.errorDescription());
            }
            case PaymentResult.Sucess sucess -> {
                log.warn("invalid state");
                return null;
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
        paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_REQUEST);

        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(),paymentId);

        if (paymentResult instanceof PaymentResult.Sucess) {//payment.setStatus(PaymentStatus.CAPTURED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCreatedAt(LocalDateTime.now());
            log.info("Payment captured successfully paymentId : {}", paymentId);
        } else if (paymentResult instanceof PaymentResult.Failure failure) {//payment.setStatus(PaymentStatus.FAILED);
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDetails(failure.errorDescription());
            log.warn("Payment failed paymentId : {}", paymentId);
        }
        payment = paymentRepository.save(payment);

        return paymentMapper.toResponce(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorizing(UUID PaymentId, boolean approve, String bankRef, String errorCode, String errorDescription) {

        Payment payment = paymentRepository.findById(PaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Payment Record","".valueOf(PaymentId)));

        if(payment.getStatus() !=  PaymentStatus.AUTHORIZING){
            log.warn("payment is not in Authorizing state, paymentId : {}, status: {}", PaymentId,payment.getStatus());
            return;
        }

        OrderRecord orderRecord = payment.getOrder();

        if(approve){
            paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

            //Auto capture
            paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult = paymentGatewayRouter.capture(payment.getMethod(),payment.getId());

            if(captureResult instanceof PaymentResult.Sucess sucess){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            } else if (captureResult instanceof PaymentResult.Failure failure) {
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDetails(failure.errorDescription());

            }
        }
        else{
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDetails(errorDescription);
        }

        paymentRepository.save(payment);
        orderRecordRepository.save(orderRecord);



    }
}
