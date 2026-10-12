package com.ajay.developer.razorpay.merchant.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisApiKeyCache implements ApiKeyCache {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private static final String PREFIX = "apikey:";
    private static final Duration TTl = Duration.ofMinutes(5);
    @Override
    public Optional<ApiKeyCacheEntry> get(String keyId) {
        try{
            String json =  stringRedisTemplate.opsForValue().get(PREFIX + keyId);
            if (json == null){
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(json, ApiKeyCacheEntry.class));
        }catch (Exception e){
            log.warn("Apikey cache read failed for keyId : {}",keyId);
        }
        return Optional.empty();
    }

    @Override
    public void put(String keyId, ApiKeyCacheEntry entry) {
        try{
            stringRedisTemplate.opsForValue().set(PREFIX+keyId,
                    objectMapper.writeValueAsString(entry),
                    TTl);
        }catch (Exception e){
            log.warn("Apikey cache write failed for keyId : {}",keyId);
        }
    }

    @Override
    public void evict(String keyId) {
        stringRedisTemplate.delete(PREFIX + keyId);
    }
}
