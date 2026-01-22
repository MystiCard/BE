package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.user.UserRegisterRequest;
import com.example.mysterycard.dto.response.UserResponse;
import com.example.mysterycard.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    Users requestToEntity(UserRegisterRequest request);
    UserResponse requestToResponse(Users user);
}
