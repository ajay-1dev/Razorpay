package com.ajay.developer.razorpay.merchant.mapper;

import com.ajay.developer.razorpay.merchant.dto.response.ApiKeyCreateResponce;
import com.ajay.developer.razorpay.merchant.dto.response.ApiKeyResponce;
import com.ajay.developer.razorpay.merchant.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyResponceMapper {

    ApiKeyResponce toApiResponce(ApiKey apiKey);

    List<ApiKeyResponce> toApiResponceList(List<ApiKey> apiKeyList);

    ApiKeyCreateResponce toApiKeyCreateResponce(ApiKey apiKey);



}
