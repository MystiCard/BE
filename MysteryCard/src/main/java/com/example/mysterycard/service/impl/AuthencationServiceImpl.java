package com.example.mysterycard.service.impl;

import com.example.mysterycard.configuration.PasswordConfig;
import com.example.mysterycard.dto.request.LoginRequest;
import com.example.mysterycard.dto.request.LogoutRequest;
import com.example.mysterycard.dto.request.RefreshAccessTokenRequest;
import com.example.mysterycard.dto.response.LoginResponse;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.repository.RoleRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.AuthencationSevice;
import com.example.mysterycard.service.RefreshTokenService;
import com.example.mysterycard.service.TokenService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Users user = usersRepo.findByEmail(auth.getPrincipal().getAttribute("email"));
        if(user == null){
            user = Users.builder()
                    .email(auth.getPrincipal().getAttribute("email"))
                    .name(auth.getPrincipal().getAttribute("name"))
                    .avatarUrl(auth.getPrincipal().getAttribute("picture"))
                    .rolelist(Set.of(roleRepo.findByRoleCode("USER")))
                    .build();
            usersRepo.save(user);
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
}
