package com.ajay.developer.razorpay.merchant.security;

import com.ajay.developer.razorpay.common.exception.ResourceNotFoundException;
import com.ajay.developer.razorpay.merchant.entity.AppUser;
import com.ajay.developer.razorpay.merchant.repository.AppUserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtMerchantContextFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AppUserRepository appUserRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final MerchantContext merchantContext;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Incomming request : {}", request.getRequestURI());
            final String authorizationHeader = request.getHeader("Authorization");
            log.info("authorizationHeader with header : {}", authorizationHeader);
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authorizationHeader.split("Bearer ")[1];
            log.info("jwt : {}", jwt);
            Claims claims = jwtUtil.verifyAccessToken(jwt);
            String email = claims.getSubject();

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var authentication = new UsernamePasswordAuthenticationToken(email, null, List.of(
                        new SimpleGrantedAuthority("ROLE_"+jwtUtil.getRole(claims))
                ));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                var  auth = SecurityContextHolder.getContext().getAuthentication();
                merchantContext.setMerchantId(jwtUtil.getMerchantId(claims));
            }


            filterChain.doFilter(request, response);
        }catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

    }
}
