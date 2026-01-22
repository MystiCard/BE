package com.example.mysterycard.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class RoleRequest {
    @NotNull(message = "Role Code not null")
    @Size(min = 5,  max = 50,message = "Role code must be from 5 to 50 character")
    private String roleCode;
    @Size(min = 5,  max = 50,message = "Role Name must be from 5 to 50 character")
    @NotNull(message = "Role Name not null")
    private String roleName;
    private String description;
}
