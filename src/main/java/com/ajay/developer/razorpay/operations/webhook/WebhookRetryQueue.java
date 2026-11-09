package com.ajay.developer.razorpay.operations.webhook;

import com.ajay.developer.razorpay.operations.entity.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebhookRetryQueue {
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${app.webhook.delivery.redis-key:webhook-retry}")
    private String key;

    public void enqueue(UUID webhookEventId, LocalDateTime retryAt) {
        long time = getTime(retryAt);
        stringRedisTemplate.opsForZSet().add(key, webhookEventId.toString(), time);
        log.info("Enqueued a webhook event with id : {}",webhookEventId);
    }


    private static long getTime(LocalDateTime retryAt) {

        return retryAt.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public Set<UUID> pollDue(int batchSize) {
        long now = getTime(LocalDateTime.now());
        var due = stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key,0,now,0,batchSize);
        due.forEach(value -> {
            stringRedisTemplate.opsForZSet().remove(key, value.toString());
        });
        return due.stream()
                .map(uuid -> UUID.fromString(uuid.getValue()))
                .collect(Collectors.toSet());
    }
}
