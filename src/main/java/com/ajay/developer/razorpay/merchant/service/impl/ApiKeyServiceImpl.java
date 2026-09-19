package com.ajay.developer.razorpay.merchant.service.impl;

import com.ajay.developer.razorpay.common.exception.ResourceNotFoundException;
import com.ajay.developer.razorpay.common.util.RandomizerUtil;
import com.ajay.developer.razorpay.merchant.dto.request.CreateApiKeyRequest;
import com.ajay.developer.razorpay.merchant.dto.response.ApiKeyCreateResponce;
import com.ajay.developer.razorpay.merchant.dto.response.ApiKeyResponce;
import com.ajay.developer.razorpay.merchant.entity.ApiKey;
import com.ajay.developer.razorpay.merchant.entity.Merchant;
import com.ajay.developer.razorpay.merchant.mapper.ApiKeyResponceMapper;
import com.ajay.developer.razorpay.merchant.repository.ApiKeyRepository;
import com.ajay.developer.razorpay.merchant.repository.MerchantRepository;
import com.ajay.developer.razorpay.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final MerchantRepository merchantRepository;
    private final ApiKeyResponceMapper apiKeyResponceMapper;

    @Override
    @Transactional
    public ApiKeyCreateResponce create(UUID merchantId, CreateApiKeyRequest createApiKeyRequest) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("merchant_not found","".valueOf(merchantId)));

        String keyId = "rzp_"+createApiKeyRequest.environment().name().toLowerCase()+"_"+ RandomizerUtil.randomBase64(24);
        String keySecret = RandomizerUtil.randomBase64(40);
        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .environment(createApiKeyRequest.environment())
                .keyId(keyId)
                .keySecretHash(keySecret)
                .build();
        apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponce(apiKey.getId(),keyId,keySecret,apiKey.getEnvironment());

    }

    @Override
    public List<ApiKeyResponce> list(UUID merchantId){
        List<ApiKey> apiKeys = apiKeyRepository.findAllByMerchant_Id(merchantId);
//        List<ApiKeyResponce> apiKeyResponceList = apiKeys.stream()
//                .map(apiKey -> new ApiKeyResponce(apiKey.getId(),
//                apiKey.getKeyId(),
//                apiKey.getEnvironment(),
//                apiKey.getEnabled(),
//                apiKey.getLastUsedAt(),
//                apiKey.getCreatedAt()))
//                .toList();
//        return  apiKeyResponceList;
        return apiKeyResponceMapper.toApiResponceList(apiKeys);

    }


    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(apikey1 -> apikey1.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("merchant_not found","".valueOf(merchantId)));
        apiKey.setEnabled(false);
        apiKeyRepository.save(apiKey);
    }

    @Override
    @Transactional
    public ApiKeyCreateResponce rotate(UUID merchantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(apikey1 -> apikey1.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("merchant_not found","".valueOf(merchantId)));

        if(!apiKey.getEnabled()){
            throw new ResourceNotFoundException("cannot rotate disabled key ","".valueOf(keyId));
        }


        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        String keySecret = RandomizerUtil.randomBase64(40);
        apiKey.setKeySecretHash(keySecret);
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiredAt(LocalDateTime.now().plusHours(24));
        apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponce(apiKey.getId(),apiKey.getKeyId(),keySecret,apiKey.getEnvironment());

    }
}
