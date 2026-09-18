package com.ajay.developer.razorpay.merchant.service;

import com.ajay.developer.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.ajay.developer.razorpay.merchant.dto.response.ApiKeyCreateResponce;
import com.ajay.developer.razorpay.merchant.dto.response.ApiKeyResponce;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponce create(UUID merchantId, @Valid CreateApiKeyRequest createApiKeyRequest);

    List<ApiKeyResponce> list(UUID merchantId);

    void revoke(UUID merchantId, UUID keyId);

    ApiKeyCreateResponce rotate(UUID merchantId, UUID keyId);
}
