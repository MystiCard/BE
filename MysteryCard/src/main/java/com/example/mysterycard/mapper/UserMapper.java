package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.user.UserRegisterRequest;
import com.example.mysterycard.dto.response.UserResponse;
import com.example.mysterycard.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {WalletMapper.class})
public interface UserMapper {
    Users requestToEntity(UserRegisterRequest request);
    @Mapping(target = "walletResponse",source = "wallet")
    UserResponse requestToResponse(Users user);
}
