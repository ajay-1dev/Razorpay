package com.ajay.developer.razorpay.payment.config;

import com.ajay.developer.razorpay.common.enums.PaymentMethod;
import com.ajay.developer.razorpay.payment.gateway.PaymentAdapter;
import com.ajay.developer.razorpay.payment.gateway.adapters.CardPaymentAdapter;
import com.ajay.developer.razorpay.payment.gateway.adapters.NetbankingAdapter;
import com.ajay.developer.razorpay.payment.gateway.adapters.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {
private final NetbankingAdapter netbankingAdapter;
private final UpiPaymentAdapter upiPaymentAdapter;
private final CardPaymentAdapter cardPaymentAdapter;
    @Bean
    public Map<PaymentMethod, PaymentAdapter>  paymentAdapterMap() {
        return Map.of(PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.UPI, upiPaymentAdapter,
                PaymentMethod.NETBANKING, netbankingAdapter);
    }
}
