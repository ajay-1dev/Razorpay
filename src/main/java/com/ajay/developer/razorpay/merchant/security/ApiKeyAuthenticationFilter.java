package com.ajay.developer.razorpay.merchant.security;

import com.ajay.developer.razorpay.common.exception.RateLimitException;
import com.ajay.developer.razorpay.common.ratelimit.RateLimitResult;
import com.ajay.developer.razorpay.common.ratelimit.RateLimiter;
import com.ajay.developer.razorpay.merchant.cache.ApiKeyCache;
import com.ajay.developer.razorpay.merchant.cache.ApiKeyCacheEntry;
import com.ajay.developer.razorpay.merchant.cache.RedisApiKeyCache;
import com.ajay.developer.razorpay.merchant.entity.ApiKey;
import com.ajay.developer.razorpay.merchant.mapper.ApiKeyResponceMapper;
import com.ajay.developer.razorpay.merchant.repository.ApiKeyRepository;
import com.ajay.developer.razorpay.merchant.service.ApiKeyService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;
    private final JwtUtil jwtUtil;
    private  final String BASIC_PREFIX = "Basic ";
    private final ApiKeyRepository  apiKeyRepository;
    private final PasswordEncoder passwordEncoder;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final ApiKeyCache apiKeyCache;
    private final ApiKeyResponceMapper apiKeyResponceMapper;
    private final RateLimiter rateLimiter;
    @Value("${app.rate-limit.use-case.api-key.api-per-minute:10}")
    private Integer requestsPerMinute;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

    try {

        log.info("Incomming request {}", request.getRequestURI());
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith(BASIC_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String[] credentials = decode(authorizationHeader);
        if (credentials == null) {
            throw new BadCredentialsException("Bad credentials");
        }

        String keyId = credentials[0];
        String rawSecret = credentials[1];

//        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
//                .orElseThrow(() -> new BadCredentialsException("Invaild or missing api key"));

        ApiKeyCacheEntry apiKeyCacheEntry = apiKeyCache.get(keyId)
                .orElseGet(() -> loadAndGet(keyId));

        if (apiKeyCache == null || !apiKeyCacheEntry.enabled()|| !secretMatches(rawSecret, apiKeyCacheEntry)) {
            throw new BadCredentialsException("Invaild or missing api key");
        }

        RateLimitResult rateLimitResult = rateLimiter.check("apikey:"+keyId,requestsPerMinute,60);

        if(!rateLimitResult.isAllowed()) {
            log.warn("to many api key requests: {}",keyId);
            throw new RateLimitException("Too many requests",rateLimitResult.retryAfterSeconds());
        }

        response.setHeader("X_RateLimit-Limit",requestsPerMinute.toString());
        response.setHeader("X-RateLimit-Remaining",String.valueOf(rateLimitResult.remaning()));
        var auth = new UsernamePasswordAuthenticationToken(keyId, null, List.of(
                new SimpleGrantedAuthority("API_KEY_ROLE")
        ));

        SecurityContextHolder.getContext().setAuthentication(auth);
        merchantContext.setMerchantId(apiKeyCacheEntry.merchantId());
        merchantContext.setKeyId(apiKeyCacheEntry.keyId());
        filterChain.doFilter(request, response);

    }catch (Exception ex){
        log.error("ERROR_IN_API_KEY_FILTER ",ex.getMessage(),ex);
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }
    }



    private ApiKeyCacheEntry loadAndGet(String keyId){
        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
                .orElse(null);
        if(apiKey == null){
            return null;
        }
        ApiKeyCacheEntry apiKeyCacheEntry = apiKeyResponceMapper.toApiKeyCacheEntry(apiKey);
        apiKeyCache.put(keyId, apiKeyCacheEntry);
        return apiKeyCacheEntry;
    }

    private boolean secretMatches(String rawSecret, ApiKeyCacheEntry apiKey) {
        if( passwordEncoder.matches(rawSecret,apiKey.keySecretHash())){
            return true;
        };

//        boolean isInGracePeriod = apiKey.getGracePeriodExpiredAt() != null && LocalDateTime.now().isBefore(apiKey.getGracePeriodExpiredAt());

        return apiKey.isInGracePeriod()
                && apiKey.previousKeySecretHash() != null
                && passwordEncoder.matches(rawSecret,apiKey.previousKeySecretHash());
    }

    private String[] decode(String authorizationHeader) {
        String encoded =  authorizationHeader.substring(BASIC_PREFIX.length());
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

        int colon = decoded.indexOf(":");
        if(colon == -1) {
            return null;
        }
        return new String[]{decoded.substring(0, colon), decoded.substring(colon + 1)};

    }
}
