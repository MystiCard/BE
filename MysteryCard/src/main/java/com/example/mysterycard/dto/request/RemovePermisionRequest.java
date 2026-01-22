package com.example.mysterycard.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class RemovePermisionRequest {
    private List<String> permisionCode;
    private String roleCode;
}
