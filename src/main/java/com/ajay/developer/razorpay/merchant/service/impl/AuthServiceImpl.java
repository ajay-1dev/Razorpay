package com.ajay.developer.razorpay.merchant.service.impl;

import com.ajay.developer.razorpay.common.enums.MerchantStatus;
import com.ajay.developer.razorpay.common.enums.UserRole;
import com.ajay.developer.razorpay.common.exception.DuplicateResourceException;
import com.ajay.developer.razorpay.merchant.dto.request.MerchantSignupRequest;
import com.ajay.developer.razorpay.merchant.dto.response.MerchantResponse;
import com.ajay.developer.razorpay.merchant.entity.AppUser;
import com.ajay.developer.razorpay.merchant.entity.Merchant;
import com.ajay.developer.razorpay.merchant.repository.AppUserRepository;
import com.ajay.developer.razorpay.merchant.repository.MerchantRepository;
import com.ajay.developer.razorpay.merchant.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final MerchantRepository merchantRepository;

    @Override
    @Transactional
     public MerchantResponse signUp(MerchantSignupRequest merchantSignupRequest){
        if(merchantRepository.existsByEmail(merchantSignupRequest.email())){
           throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL","Merchant with Email already exists: "+merchantSignupRequest.email());
        }

        Merchant merchant = Merchant.builder()
                .businessName(merchantSignupRequest.businessName())
                .businessType(merchantSignupRequest.businessType())
                .name(merchantSignupRequest.name())
                .email(merchantSignupRequest.email())
                .status(MerchantStatus.PENDING_KYC)
                .build();
        merchant = merchantRepository.save(merchant);
        AppUser appUser = AppUser.builder()
                .email(merchantSignupRequest.email())
                .merchant(merchant)
                .passwordHash(merchantSignupRequest.password())
                .role(UserRole.OWNER)
                          .build();
        appUserRepository.save(appUser);
        return new MerchantResponse(merchant.getId(),merchant.getName(),merchant.getEmail(),merchant.getBusinessName(),merchant.getBusinessType(),merchant.getStatus());
    }
}
