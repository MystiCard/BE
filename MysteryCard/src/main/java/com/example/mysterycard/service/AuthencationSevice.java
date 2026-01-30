package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.EmailVerifyRequest;
import com.example.mysterycard.dto.request.LoginRequest;
import com.example.mysterycard.dto.request.LogoutRequest;
import com.example.mysterycard.dto.request.RefreshAccessTokenRequest;
import com.example.mysterycard.dto.response.EmailVerifyResponse;
import com.example.mysterycard.dto.response.LoginResponse;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface AuthencationSevice {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse loginGoogle(OAuth2AuthenticationToken auth) throws JOSEException;
    String refreshAccessToken(RefreshAccessTokenRequest request) throws JOSEException;
    void Logout();
    EmailVerifyResponse verifyEmail(EmailVerifyRequest request) throws JOSEException;
    void sendVerifyCode(String email);
}
