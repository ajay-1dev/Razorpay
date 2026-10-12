package com.ajay.developer.razorpay.merchant.service.impl;

import com.ajay.developer.razorpay.common.exception.ResourceNotFoundException;
import com.ajay.developer.razorpay.merchant.entity.AppUser;
import com.ajay.developer.razorpay.merchant.repository.AppUserRepository;
import com.ajay.developer.razorpay.merchant.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserServiceImpl implements AppUserService , UserDetailsService {
    private final AppUserRepository appUserRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND","Email : "+email));
    }

}
