package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.AddPermisionToRoleRequest;
import com.example.mysterycard.dto.request.RemovePermisionRequest;
import com.example.mysterycard.dto.request.RoleRequest;
import com.example.mysterycard.dto.response.RoleResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface RoleService {
    RoleResponse getRoleByCode(String roleCode);
    Page<RoleResponse> getALLRole(boolean active,int page, int size);
    void deleteRoleByCode(String roleCode);
    Page<RoleResponse> getRoleByUserId(boolean active, UUID userId,int page, int size);
    RoleResponse addRole(RoleRequest request);
    RoleResponse removePermisionByRoleCodeAndPermisionCode(RemovePermisionRequest request);
    RoleResponse addPermision(AddPermisionToRoleRequest request);
    void activeRole(String roleCde);
}
