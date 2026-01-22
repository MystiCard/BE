package com.example.mysterycard.dto.request.user;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AddRemoveRoleRequest {
    private List<String> roleCode;
    private UUID userId;
}
