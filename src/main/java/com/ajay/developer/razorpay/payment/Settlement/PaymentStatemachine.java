package com.ajay.developer.razorpay.payment.Settlement;

import com.ajay.developer.razorpay.common.enums.PaymentEvent;
import com.ajay.developer.razorpay.common.enums.PaymentStatus;
import com.ajay.developer.razorpay.common.exception.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStatemachine {
    private record Transition(PaymentStatus paymentStatus, PaymentEvent paymentEvent) {}
    private static final Map<Transition,PaymentStatus> TRANSITION = Map.ofEntries(
            Map.entry(new Transition(PaymentStatus.CREATED,PaymentEvent.AUTHORIZE_ATTEMPT),PaymentStatus.AUTHORIZING),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING,PaymentEvent.AUTHORIZE_SUCCESS),PaymentStatus.AUTHORIZED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING,PaymentEvent.AUTHORIZE_FAIL),PaymentStatus.FAILED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZED,PaymentEvent.CAPTURE_REQUEST),PaymentStatus.CAPTURING),
            Map.entry(new Transition(PaymentStatus.CAPTURING,PaymentEvent.CAPTURE_SUCCESS),PaymentStatus.CAPTURED),
            Map.entry(new Transition(PaymentStatus.CAPTURING,PaymentEvent.CAPTURE_FAIL),PaymentStatus.AUTHORIZED),
            Map.entry(new Transition(PaymentStatus.CAPTURED,PaymentEvent.REFUND_INIT),PaymentStatus.PARTIALLY_REFUNDED),
            Map.entry(new Transition(PaymentStatus.CAPTURED,PaymentEvent.SETTLE),PaymentStatus.SETTLED),
            Map.entry(new Transition(PaymentStatus.PARTIALLY_REFUNDED,PaymentEvent.REFUND_COMPLETE),PaymentStatus.REFUNDED),
            Map.entry(new Transition(PaymentStatus.CAPTURED,PaymentEvent.REFUND_COMPLETE),PaymentStatus.REFUNDED),
            Map.entry(new Transition(PaymentStatus.CREATED,PaymentEvent.CANCEL),PaymentStatus.CANCELED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING,PaymentEvent.CANCEL),PaymentStatus.CANCELED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZED,PaymentEvent.CAPTURE_TIMEOUT),PaymentStatus.AUTH_EXPIRED),
            Map.entry(new Transition(PaymentStatus.SETTLED,PaymentEvent.REFUND_INIT),PaymentStatus.PARTIALLY_REFUNDED)
            );

    public PaymentStatus transition(PaymentStatus paymentStatus, PaymentEvent paymentEvent) {
        PaymentStatus next = TRANSITION.get(new  Transition(paymentStatus,paymentEvent));
        if(next==null){
            throw new InvalidStateTransitionException(paymentStatus.name(), paymentEvent.name());
        }
        return next;
    }

}
