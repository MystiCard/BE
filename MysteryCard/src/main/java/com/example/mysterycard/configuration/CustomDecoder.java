package com.example.mysterycard.configuration;


import com.example.mysterycard.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
@Component
@RequiredArgsConstructor
public class CustomDecoder implements JwtDecoder {
    private final TokenService tokenService;
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Override
    public Jwt decode(String token) throws JwtException {
        boolean isValid;
        try {
             isValid = tokenService.validateToken(token);
        }catch (Exception e) {
            throw new JwtException("Invalid JWT token", e);
        }
        if(!isValid) {
            throw new JwtException("Unauthorized");
        }

        SecretKey secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "hmacSHA512");
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS512).build();
        return  jwtDecoder.decode(token);
    }
}
