package com.example.mysterycard.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleResponse {
    private String roleCode;
    private String roleName;
    private String description;
    private Set<PermisionResponse> permisionResponse;
}
