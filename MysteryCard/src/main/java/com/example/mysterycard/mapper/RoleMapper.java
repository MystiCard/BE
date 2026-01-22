package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.RoleRequest;
import com.example.mysterycard.dto.response.RoleResponse;
import com.example.mysterycard.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse enityToReponse(Role role);
    Role requestToEntity(RoleRequest request);
}
