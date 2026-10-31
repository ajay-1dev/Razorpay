package com.ajay.developer.razorpay.payment.outbox;


import com.ajay.developer.razorpay.common.enums.OutboxStatus;
import com.ajay.developer.razorpay.payment.entity.OutboxEvent;
import com.ajay.developer.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OutboxResultHandler {

    private final OutboxEventRepository outboxEventRepository;
    private final Integer MAX_ATTEMPTS = 3;

    @Transactional
    public void handleEventPublished(OutboxEvent events) {
        events.setStatus(OutboxStatus.PUBLISHED);
        events.setPublishedAt(LocalDateTime.now());
        outboxEventRepository.save(events);
    }


    @Transactional
    public void handleEventFailed(OutboxEvent events, String  message) {
        events.setAttempts(events.getAttempts() + 1);
        events.setLastError(message.length()< 1000 ? message:message.substring(0,1000));
        if(events.getAttempts() > MAX_ATTEMPTS) {
            events.setStatus(OutboxStatus.FAILED);
        }
        outboxEventRepository.save(events);
    }
}
