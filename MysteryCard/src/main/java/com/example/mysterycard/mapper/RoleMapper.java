package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.RoleRequest;
import com.example.mysterycard.dto.response.RoleResponse;
import com.example.mysterycard.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse enityToReponse(Role role);
    Role requestToEntity(RoleRequest request);
}
