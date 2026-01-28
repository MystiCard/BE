package com.example.mysterycard.service.impl;

import com.example.mysterycard.configuration.PasswordConfig;
import com.example.mysterycard.dto.request.EmailVerifyRequest;
import com.example.mysterycard.dto.request.LoginRequest;
import com.example.mysterycard.dto.request.LogoutRequest;
import com.example.mysterycard.dto.request.RefreshAccessTokenRequest;
import com.example.mysterycard.dto.response.EmailVerifyResponse;
import com.example.mysterycard.dto.response.LoginResponse;
import com.example.mysterycard.entity.EmailVerify;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.repository.EmailVerifyRepo;
import com.example.mysterycard.repository.RoleRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.AuthencationSevice;
import com.example.mysterycard.service.RefreshTokenService;
import com.example.mysterycard.service.TokenService;
import com.example.mysterycard.utils.EmailSender;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthencationServiceImpl implements AuthencationSevice {
     private final TokenService tokenService;
     private final RefreshTokenService refreshTokenService;
     private final UsersRepo usersRepo;
     private final PasswordEncoder passwordEncoder;
     private final RoleRepo roleRepo;
     private final EmailSender emailSender;
     private final EmailVerifyRepo emailVerifyRepo;
     @Value("${code.expired}")
     private int expired;
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Users user = usersRepo.findByEmail(loginRequest.getUsername());
        if(user == null){
            throw  new AppException(ErrorCode.INVALID_LOGIN_REQUEST);
        }
        if(!user.isActive()){
            throw new AppException(ErrorCode.USER_INACTIVE);
        }
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
        {
            throw new AppException(ErrorCode.INVALID_LOGIN_REQUEST);
        }
        try {
          return  LoginResponse.builder()
                    .accessToken(tokenService.generateToken(user))
                    .refreshToken(refreshTokenService.generateRefreshToken(user))
                    .build();
        }catch (Exception e){
            throw new JwtException("Generate token failed", e);
        }
    }

    @Override
    public LoginResponse loginGoogle(OAuth2AuthenticationToken auth) throws JOSEException {
        String email = auth.getPrincipal().getAttribute("email");
        Users user = usersRepo.findByEmail(email);
        if(user == null){
            user = Users.builder()
                    .email(email)
                    .name(auth.getPrincipal().getAttribute("name"))
                    .avatarUrl(auth.getPrincipal().getAttribute("picture"))
                    .rolelist(Set.of(roleRepo.findByRoleCode("USER")))
                    .active(false)
                    .build();
            usersRepo.save(user);
        }
        if(!user.isActive()){
           sendVerifyCode(email);
          return LoginResponse.builder()
                  .email(email)
                  .build();
        }
        return LoginResponse.builder()
                .accessToken(tokenService.generateToken(user))
                .refreshToken(refreshTokenService.generateRefreshToken(user))
                .build();
    }

    @Override
    public String refreshAccessToken(RefreshAccessTokenRequest refreshToken) throws JOSEException {
        return  refreshTokenService.refreshAccessToken(refreshToken.getRefreshToken());
    }

    @Override
    public void Logout() {
       String email = SecurityContextHolder.getContext().getAuthentication().getName();
       log.info("User {} is logging out", email);
       Users user = usersRepo.findByEmail(email);
         if(user == null){
            throw  new AppException(ErrorCode.USER_NOT_FOUND);
         }

         user.setTokenVersion(user.getTokenVersion() + 1);
            usersRepo.save(user);

    }

    @Override
    public EmailVerifyResponse verifyEmail(EmailVerifyRequest request) throws JOSEException {

        EmailVerify emailVerify = emailVerifyRepo.findByEmail(request.getEmail());
        if(emailVerify == null){
            throw new AppException(ErrorCode.EMAIL_VERIFED);
        }
        String message = "";
        boolean verified = true;
        if(emailVerify.getExpiryDate().isBefore(LocalDateTime.now())){
            message = "Code is expired";
            verified = false;
        }
        if(!emailVerify.getCode().equals(request.getCode())){
            message = "Wrong code";
            verified = false;
        }
        if(!verified){
            emailVerify.setWrongNumber(emailVerify.getWrongNumber() + 1);
        }
        emailVerifyRepo.save(emailVerify);
        LoginResponse loginResponse = null;
        if(verified)
        {
            Users user = usersRepo.findByEmail(request.getEmail());
            user.setActive(true);
            usersRepo.save(user);
             loginResponse = LoginResponse.builder()
                    .accessToken(tokenService.generateToken(user))
                    .refreshToken(refreshTokenService.generateRefreshToken(user))
                    .build();
        }

        return EmailVerifyResponse.builder()
                .email(emailVerify.getEmail())
                .verified(verified)
                .message(message)
                .loginResponse(loginResponse)
                .allowInput(emailVerify.getWrongNumber() < 5)
                .build();
    }
  @Transactional
    @Override
    public void sendVerifyCode(String email) {
        emailVerifyRepo.deleteAllByEmail(email);
        String code = generate6DigitCode();
        emailSender.sendSimpleMail(email,"Your OTP Code",code);
        EmailVerify emailVerify = EmailVerify.builder()
                .code(code)
                .email(email)
                .expiryDate(LocalDateTime.now().plusMinutes(expired))
                .build();
        emailVerifyRepo.save(emailVerify);
    }

    public String generate6DigitCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
