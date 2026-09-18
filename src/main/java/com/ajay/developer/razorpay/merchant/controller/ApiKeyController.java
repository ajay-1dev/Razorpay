package com.ajay.developer.razorpay.merchant.controller;

import com.ajay.developer.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.ajay.developer.razorpay.merchant.dto.response.ApiKeyCreateResponce;
import com.ajay.developer.razorpay.merchant.dto.response.ApiKeyResponce;
import com.ajay.developer.razorpay.merchant.entity.ApiKey;
import com.ajay.developer.razorpay.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchant/{merchant_id}/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponce> createApiKey(@PathVariable UUID merchant_id, @RequestBody @Valid CreateApiKeyRequest  createApiKeyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.create(merchant_id,createApiKeyRequest));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponce>> list(@PathVariable UUID merchant_id) {
        return ResponseEntity.ok(apiKeyService.list(merchant_id));
    }

    @DeleteMapping("/{keyid}")
    public ResponseEntity<Void> revoke(@PathVariable UUID merchant_id, @PathVariable("keyid") UUID key_id) {
        apiKeyService.revoke(merchant_id,key_id);
        return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{keyid}/rotate")
    public ResponseEntity<ApiKeyCreateResponce> rotate(@PathVariable UUID merchant_id, @PathVariable("keyid") UUID key_id) {
        return ResponseEntity.ok(apiKeyService.rotate(merchant_id,key_id));
    }
}
