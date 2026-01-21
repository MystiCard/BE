package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID userId;
    private String email;
    private String password;
    private String name;
    private int tokenVersion = 0;
    private String avatarUrl;
    private String address;
    private List<String> roles;
}
