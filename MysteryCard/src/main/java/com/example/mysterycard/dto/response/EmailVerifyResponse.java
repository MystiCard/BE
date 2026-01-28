package com.example.mysterycard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailVerifyResponse {
    private String email;
    private boolean allowInput;
    private String message;
    private boolean verified;
    private LoginResponse loginResponse;
}
