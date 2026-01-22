package com.example.mysterycard.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RoleRequest {
    private String roleCode;
    private String roleName;
    private String description;
    List<String> permisionCode;
}
