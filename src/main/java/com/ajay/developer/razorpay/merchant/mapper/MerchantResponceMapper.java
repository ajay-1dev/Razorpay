package com.ajay.developer.razorpay.merchant.mapper;

import com.ajay.developer.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ajay.developer.razorpay.merchant.dto.response.MerchantResponse;
import com.ajay.developer.razorpay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantResponceMapper {

    MerchantResponse toMerchantResponce(Merchant merchant);

    Merchant toEntity(MerchantSignupRequest merchantSignupRequest);
}
