package com.example.mysterycard.service;

import com.example.mysterycard.entity.Users;
import com.nimbusds.jose.JOSEException;

public interface RefreshTokenService {
    String generateRefreshToken(Users user);
    String refreshAccessToken(String refreshToken) throws JOSEException;
}
