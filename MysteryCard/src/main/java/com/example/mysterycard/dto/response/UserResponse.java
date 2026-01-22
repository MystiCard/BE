package com.example.mysterycard.dto.response;

import com.example.mysterycard.entity.Role;
import com.example.mysterycard.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID userId;
    private String email;
    private Gender gender;
    private String name;
    private String avatarUrl;
    private String address;
    private String phone;
}
