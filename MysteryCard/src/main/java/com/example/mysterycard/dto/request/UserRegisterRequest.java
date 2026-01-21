package com.example.mysterycard.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRegisterRequest {
    private String username;
    private String email;
    private String password;
    private MultipartFile image;
    private String address;
}
