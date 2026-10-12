package com.ajay.developer.razorpay.merchant.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    @Value("${jwt_secret_key}")
    private String secretKey;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    public String generateAceesToken(String email, UUID merchantId,String role){
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("role",role)
                .claim("merchant_id",merchantId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(60 * 100)))
                .signWith(getSecretKey())
                .compact();
    }

    public Claims verifyAccessToken(String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    public String getRole(Claims claims){
        return claims.get("role").toString();
    }

    public UUID getMerchantId(Claims claims){
        return UUID.fromString(claims.get("merchant_id").toString());
    }




}
