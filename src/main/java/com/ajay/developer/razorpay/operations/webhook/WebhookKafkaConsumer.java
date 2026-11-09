package com.ajay.developer.razorpay.operations.webhook;

import com.ajay.developer.razorpay.common.dto.WebhookTarget;
import com.ajay.developer.razorpay.common.enums.WebhookEventStatus;
import com.ajay.developer.razorpay.common.util.SignerUtil;
import com.ajay.developer.razorpay.merchant.service.MerchantLookupService;
import com.ajay.developer.razorpay.operations.entity.WebhookEvent;
import com.ajay.developer.razorpay.operations.repository.WebhookEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebhookKafkaConsumer {

    private final MerchantLookupService merchantLookupService;
    private final ObjectMapper objectMapper;
    private final SignerUtil signerUtil;
    private final WebhookEventRepository webhookEventRepository;
    private final WebhookRetryQueue webhookRetryQueue;

    @KafkaListener(topics = {
            "${app.kafka.topics.payment:payments.events}",
            "${app.kafka.topics.order:order.events}",
            "${app.kafka.topics.refund:refund.events}",
            "${app.kafka.topics.settlement:settlement.events}"
    })
    public void onWebhookEvent(ConsumerRecord<String, Map<String,Object>> record, Acknowledgment ack) {

        Map<String,Object> envolope =  record.value();
        Map<String,Object> data = (Map<String,Object>) envolope.get("data");
        String evnentType = (String) envolope.get("eventType");
        Object merchantIdRaw = data.get("merchantId");

        if(merchantIdRaw == null){
            log.warn("no merchant id found for the event {} :", evnentType);
            ack.acknowledge();
            return;
        }

        UUID merchantId = UUID.fromString(merchantIdRaw.toString());

        List<WebhookTarget> targets = merchantLookupService.getActiveConfigsForEvent(merchantId, evnentType);
        if(targets.isEmpty()){
            log.warn("no targets found for the event {} :", evnentType);
            ack.acknowledge();
            return;
        }

        Map<String,Object> signatureData = Map.of("event" , envolope , "payload" , data);
        String signaturejson = objectMapper.writeValueAsString(signatureData);
//
//        List<WebhookEvent> webhookEvents =  new ArrayList<>();
        for(WebhookTarget target : targets){
            String signature = signerUtil.sign(signaturejson,target.webhookSecret());
            WebhookEvent webhookEvent = WebhookEvent.builder()
                    .merchantId(merchantId)
                    .eventType(evnentType)
                    .payload(data)
                    .targetUrl(target.targetUrl())
                    .signature(signature)
                    .status(WebhookEventStatus.PENDING)
                    .nextRetryAt(LocalDateTime.now())
                    .build();

//            webhookEvents.add(webhookEvent);
            webhookEventRepository.save(webhookEvent);
            webhookRetryQueue.enqueue(webhookEvent.getId(), webhookEvent.getNextRetryAt());
            log.info("Created a webhook event with id: {}", webhookEvent.getId());
        }
        ack.acknowledge();
//        List<WebhookEvent> webhookEventList = webhookEventRepository.saveAll(webhookEvents);
//        for(WebhookEvent webhookEvent : webhookEventList){
//            webhookRetryQueue.enqueue(webhookEvent.getId(),webhookEvent.getNextRetryAt());
//        }


    }
}