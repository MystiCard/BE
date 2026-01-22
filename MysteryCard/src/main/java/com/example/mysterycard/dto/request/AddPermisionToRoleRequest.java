package com.example.mysterycard.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class AddPermisionToRoleRequest {
    private String roleCode;
    List<String> permisionCode;
}
