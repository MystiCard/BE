package com.example.mysterycard.dto.request;

import lombok.Data;

@Data
public class EmailVerifyRequest {
    private String email;
    private String code;
}
