package com.example.mysterycard.service.impl;

import com.example.mysterycard.entity.RefreshToken;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.repository.RefreshTokenRepo;
import com.example.mysterycard.service.RefreshTokenService;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final TokenServiceImpl tokenService;
    private final RefreshTokenRepo refreshTokenRepo;
    @Value("${jwt.refreshExpiration}")
    private Long REFRESH_EXPIRATION_TIME;
    @Override
    public String generateRefreshToken(Users user) {
        refreshTokenRepo.deleteAll(refreshTokenRepo.findAllByUser(user));
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusSeconds(REFRESH_EXPIRATION_TIME))
                .build();
       refreshTokenRepo.save(refreshToken);
        return refreshToken.getToken();
    }

    @Override
    public String refreshAccessToken(String refreshToken) throws JOSEException {
        RefreshToken rf = refreshTokenRepo.findByToken(refreshToken);
        if(rf == null)
        {
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        if(rf.getExpiryDate().isBefore(LocalDateTime.now()))
        {
            refreshTokenRepo.delete(rf);
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        return tokenService.generateToken(rf.getUser());
    }
}
