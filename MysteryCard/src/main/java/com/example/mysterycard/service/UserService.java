package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.user.AddRemoveRoleRequest;
import com.example.mysterycard.dto.request.user.UserRegisterRequest;
import com.example.mysterycard.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UserService {
    UserResponse createUser(UserRegisterRequest request, MultipartFile avatar);
    UserResponse updateUser(UserRegisterRequest request, MultipartFile avatar, UUID userId);
    Page<UserResponse> getAll(boolean active, int page, int size);
    void deleteUser(UUID userId);
    UserResponse activeUser(UUID userId);
    UserResponse getMyInfor();
    UserResponse getUserById(UUID userId);
    void addRole(AddRemoveRoleRequest request);
    void removeRole(AddRemoveRoleRequest request);
}
