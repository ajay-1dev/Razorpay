package com.ajay.developer.razorpay.payment.Settlement;

import com.ajay.developer.razorpay.common.enums.PaymentEvent;
import com.ajay.developer.razorpay.common.enums.PaymentStatus;
import com.ajay.developer.razorpay.payment.entity.Payment;
import com.ajay.developer.razorpay.payment.entity.PaymentTransitionLog;
import com.ajay.developer.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentTransitionService {
    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStatemachine paymentStatemachine;

    public PaymentStatus apply(Payment payment, PaymentEvent paymentEvent){
        PaymentStatus next = paymentStatemachine.transition(payment.getStatus(),paymentEvent);
        payment.setStatus(next);
        PaymentTransitionLog paymentTransitionLog = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(paymentEvent)
                .toStatus(next)
                .actor("System") //TODO: fetch merchant context to identify actor
                .occuredAt(LocalDateTime.now())
                .build();
        paymentTransitionLogRepository.save(paymentTransitionLog);
        return next;
    }
}
