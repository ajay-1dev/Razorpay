package com.ajay.developer.razorpay.merchant.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private static final String[] JWT_ROUTES = {"/v1/auth/**","/v1/merchant/**","/v1/admin/**","/actuator/**"};
    private static final String[] API_KEY_ROUTES = {"/v1/orders/**","/v1/payments/**","/v1/vault/**"};
    private final JwtMerchantContextFilter jwtMerchantContextFilter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;


    @Bean
    public SecurityFilterChain jwtChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(JWT_ROUTES)
                .authorizeHttpRequests(auth -> {
            auth.requestMatchers("/v1/auth/signup","/v1/auth/login").permitAll()
                    .anyRequest().authenticated();
        }).csrf(crsf -> crsf.disable())
                .addFilterBefore(jwtMerchantContextFilter, UsernamePasswordAuthenticationFilter.class)
//                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public SecurityFilterChain apiKeyChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(API_KEY_ROUTES)
                .authorizeHttpRequests(auth -> { auth
//                    .requestMatchers("/v1/auth/signup","/v1/auth/login").permitAll()
                           .anyRequest().authenticated();
                }).csrf(crsf -> crsf.disable())
                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .formLogin(formLogin -> formLogin.disable())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration congif) throws Exception {
        return congif.getAuthenticationManager();
    }
}
