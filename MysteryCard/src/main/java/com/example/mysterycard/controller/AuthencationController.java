package com.example.mysterycard.controller;


import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.LoginRequest;
import com.example.mysterycard.dto.request.RefreshAccessTokenRequest;
import com.example.mysterycard.service.AuthencationSevice;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthencationController {
    private final AuthencationSevice authencationSevice;
    @PostMapping("/login")
    public ApiResponse login(@RequestBody LoginRequest loginRequest)
    {
        log.info("Login attempt for user: {}", loginRequest.getUsername());
        return ApiResponse.success(authencationSevice.login(loginRequest));
    }

   @PostMapping("/refresh-access-token")
    public ApiResponse refreshAccessToken(@RequestBody RefreshAccessTokenRequest refreshAccessTokenRequest) throws JOSEException {

            String newAccessToken = authencationSevice.refreshAccessToken(refreshAccessTokenRequest);
            return ApiResponse.success(newAccessToken);

    }
    @GetMapping("/logout")
    public ApiResponse logout()
    {
        authencationSevice.Logout();
        return ApiResponse.success("Logout successful");
    }
    @PreAuthorize("hasAuthority('HAHA') || hasRole('ADMIN')")
    @GetMapping("/test")
    public ApiResponse test()
    {
        return ApiResponse.success("Test successful");
    }
    @GetMapping("/login-google")
    public ApiResponse loginGoogle(OAuth2AuthenticationToken principal) throws JOSEException {
        log.info("Google login attempt for user: {}", principal.getName());
        return ApiResponse.success(authencationSevice.loginGoogle(principal));
    }

}
