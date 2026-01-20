package com.example.mysterycard.configuration;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;



@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(ErrorCode.INVALID_AUTHENCATION.getStatus().value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(ErrorCode.INVALID_AUTHENCATION.getCode(),ErrorCode.INVALID_AUTHENCATION.getMessage())));
        response.flushBuffer();

    }
}
