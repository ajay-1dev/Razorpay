package com.ajay.developer.razorpay.common.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.rate-limit.method",havingValue = "slidingWindow")
public class SlidingWindowRateLimiter implements RateLimiter{

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RateLimitResult check(String key, int maxAllowedRequests, long windowSeconds) {

        long nowMillis = System.currentTimeMillis();
        long windowMillis = windowSeconds * 1000;
        long flagMillis = nowMillis - windowMillis;
        String redisKey = "ratelimit:slidingWindow:" + key;
        var zSet = stringRedisTemplate.opsForZSet();
        zSet.removeRangeByScore(redisKey,Double.NEGATIVE_INFINITY,flagMillis);
        Long count = zSet.zCard(redisKey);
        long current = count == null ? 0 : count;
        if(current >= maxAllowedRequests){
            var oldest = zSet.rangeWithScores(redisKey,0,0);
            int retryAfter = 1;
            if(oldest != null  && !oldest.isEmpty()){
                Double oldScore = oldest.iterator().next().getScore();
                if(oldScore != null){
                    long windowExpiresMs = oldScore.longValue() + (windowSeconds * 1000);
                    retryAfter = (int) Math.ceil((windowExpiresMs - nowMillis)/ 1000.0);
                }
            }
            return RateLimitResult.denied(retryAfter);
        }
        zSet.add(redisKey, UUID.randomUUID().toString(),nowMillis);
        stringRedisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
        return RateLimitResult.allowed((int) (maxAllowedRequests - current - 1));
    }
}
