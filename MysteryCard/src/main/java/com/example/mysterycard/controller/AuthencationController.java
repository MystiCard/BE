package com.example.mysterycard.controller;


import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.EmailVerifyRequest;
import com.example.mysterycard.dto.request.LoginRequest;
import com.example.mysterycard.dto.request.RefreshAccessTokenRequest;
import com.example.mysterycard.dto.response.EmailVerifyResponse;
import com.example.mysterycard.dto.response.LoginResponse;
import com.example.mysterycard.service.AuthencationSevice;
import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthencationController {
    private final AuthencationSevice authencationSevice;
    private final PasswordEncoder passwordEncoder;
    @Value("${FRONTEND_URL}")
    private  String FE_URL;
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
    public RedirectView loginGoogle(OAuth2AuthenticationToken principal) throws JOSEException {
        log.info("Google login attempt for user: {}", principal.getName());
        LoginResponse loginResponse = authencationSevice.loginGoogle(principal);
        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();
        if(loginResponse.getRoleCode().equalsIgnoreCase("ADMIN"))
        {
         return  new RedirectView(FE_URL+"/admin?accessToken="+accessToken+"&refreshToken="+refreshToken);
        }
        return new RedirectView(FE_URL+"?accessToken="+accessToken+"&refreshToken="+refreshToken);
    }
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<EmailVerifyResponse>> verifyEmail(@RequestBody EmailVerifyRequest request) throws JOSEException {
        return ResponseEntity.ok(ApiResponse.success(authencationSevice.verifyEmail(request)));
    }
    @GetMapping("/send-verify-code")
    public ResponseEntity<ApiResponse<?>> sendVerifyEmail(@RequestParam String email)
    {
        authencationSevice.sendVerifyCode(email);
        return ResponseEntity.ok(ApiResponse.success("Send code verify succesfully"));
    }
    @PostMapping("/password-endcode/{password}")
    public ResponseEntity<ApiResponse<String>> passwordEncode(@PathVariable String password) {
        return ResponseEntity.ok(ApiResponse.success(passwordEncoder.encode(password)));
    }

}
