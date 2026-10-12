package com.ajay.developer.razorpay.merchant.controller;

import com.ajay.developer.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.ajay.developer.razorpay.merchant.dto.response.ApiKeyCreateResponce;
import com.ajay.developer.razorpay.merchant.dto.response.ApiKeyResponce;
import com.ajay.developer.razorpay.merchant.entity.ApiKey;
import com.ajay.developer.razorpay.merchant.security.MerchantContext;
import com.ajay.developer.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchant/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponce> createApiKey( @RequestBody @Valid CreateApiKeyRequest  createApiKeyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.create(merchantContext.getMerchantId(),createApiKeyRequest));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponce>> list() {
        return ResponseEntity.ok(apiKeyService.list(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/{keyid}")
    public ResponseEntity<Void> revoke(@PathVariable("keyid") UUID key_id) {
        apiKeyService.revoke(merchantContext.getMerchantId(),key_id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{keyid}/rotate")
    public ResponseEntity<ApiKeyCreateResponce> rotate( @PathVariable("keyid") UUID key_id) {
        return ResponseEntity.ok(apiKeyService.rotate(merchantContext.getMerchantId(),key_id));
    }
}
