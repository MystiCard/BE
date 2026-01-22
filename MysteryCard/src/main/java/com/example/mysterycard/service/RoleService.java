package com.example.mysterycard.service;

import com.example.mysterycard.dto.response.RoleResponse;
import org.springframework.data.domain.Page;

public interface RoleService {
    RoleResponse getRoleByCode(String roleCode);
    Page<RoleResponse> getALLRole();
    void deleteRoleByCode(String roleCode);
}
