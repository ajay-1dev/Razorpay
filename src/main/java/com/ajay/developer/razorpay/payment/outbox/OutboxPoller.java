package com.ajay.developer.razorpay.payment.outbox;


import com.ajay.developer.razorpay.common.config.KafkaProperties;
import com.ajay.developer.razorpay.common.enums.OutboxStatus;
import com.ajay.developer.razorpay.payment.entity.OutboxEvent;
import com.ajay.developer.razorpay.payment.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final OutboxResultHandler outboxResultHandler;

    @Scheduled(fixedRate = 5000)
    public void poll()  {



        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);

        for(OutboxEvent events: pendingEvents) {
            try{
            String topic = kafkaProperties.topicFor(events.getAggregateType());

            String key = extractMerchantId(events.getPayload());

                Map<String, Object> envelope = Map.of(
                        "eventType", events.getEventType(),
                        "aggregateType", events.getAggregateType().name(),
                        "aggregateId", events.getAggregateId().toString(),
                        "data", events.getPayload()
                );

            kafkaTemplate.send(topic, key, envelope)
                    .get(5, TimeUnit.SECONDS);

            outboxResultHandler.handleEventPublished(events);
        }catch (Exception e) {
                log.error("Error while polling outbox events check the com.ajay.developer.razorpay.payment.outbox.OutboxPoller");
                log.error("Outbox event failed, eventId: {}, attempts {}",events.getId(), events.getAttempts());
                outboxResultHandler.handleEventFailed(events,e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }




    private String extractMerchantId(Map<String, Object> payload) {

        Object merchantId = payload.get("merchantId");

        if (merchantId == null) {
            throw new IllegalArgumentException(
                    "merchantId is missing from outbox payload"
            );
        }

        return merchantId.toString();
    }


}
