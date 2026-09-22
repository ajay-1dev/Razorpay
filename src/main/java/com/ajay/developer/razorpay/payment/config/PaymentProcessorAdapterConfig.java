package com.ajay.developer.razorpay.payment.config;

import com.ajay.developer.razorpay.common.enums.PaymentMethod;
import com.ajay.developer.razorpay.payment.processor.PaymentProcessor;
import com.ajay.developer.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.ajay.developer.razorpay.payment.processor.strategy.NetbankingPaymentProcessor;
import com.ajay.developer.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorAdapterConfig {

    private final CardPaymentProcessor cardPaymentProcessor;
    private final UpiPaymentProcessor upiPaymentProcessor;
    private final NetbankingPaymentProcessor netbankingPaymentProcessor;
    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap() {
        return Map.of(PaymentMethod.CARD,cardPaymentProcessor,
                PaymentMethod.NETBANKING,netbankingPaymentProcessor,
                PaymentMethod.UPI,upiPaymentProcessor);
    }
}
