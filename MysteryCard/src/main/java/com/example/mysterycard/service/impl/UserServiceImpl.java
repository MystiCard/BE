package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.user.AddRemoveRoleRequest;
import com.example.mysterycard.dto.request.user.UserRegisterRequest;
import com.example.mysterycard.dto.response.RoleResponse;
import com.example.mysterycard.dto.response.UserResponse;
import com.example.mysterycard.entity.Role;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.enums.Gender;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.PermisionMapper;
import com.example.mysterycard.mapper.RoleMapper;
import com.example.mysterycard.mapper.UserMapper;
import com.example.mysterycard.repository.RoleRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.RoleService;
import com.example.mysterycard.service.UserService;
import com.example.mysterycard.service.WalletService;
import com.example.mysterycard.utils.CloudiaryUtils;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service

public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UsersRepo usersRepo;
    private final RoleMapper roleMapper;
    private final PermisionMapper permisionMapper;
    private final RoleRepo roleRepo;
    private final CloudiaryUtils cloudiaryUtils;
    private final PasswordEncoder passwordEncoder;
    @Value("${avartar.default.male}")
    private String maleAvartar;
    @Value("${avartar.default.female}")
    private String femaleAvartar;
    private final WalletService walletService;

    @Override
    @Transactional
    public UserResponse createUser(UserRegisterRequest request, MultipartFile avatar) {
        if (usersRepo.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (usersRepo.existsByPhone(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
        Users user = userMapper.requestToEntity(request);
        user.setRolelist(new HashSet<>());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role roleUser = roleRepo.findByRoleCode("USER");
        user.getRolelist().add(roleUser);
        if (avatar == null) {
            if (request.getGender().equals(Gender.FEMALE)) {
                user.setAvatarUrl(femaleAvartar);
            } else {
                user.setAvatarUrl(maleAvartar);
            }
        } else {
            user.setAvatarUrl(cloudiaryUtils.uploadImage(avatar));
        }
        usersRepo.save(user);
        UserResponse response = userMapper.requestToResponse(user);
        response.setWalletResponse(walletService.createWallet(user));
        return response;
    }

    @Override
    public UserResponse updateUser(UserRegisterRequest request, MultipartFile avatar, UUID userId) {
        Users user = usersRepo.findByUserId(userId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (usersRepo.existsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        if (avatar != null) {
            user.setAvatarUrl(cloudiaryUtils.uploadImage(avatar));
        }
        if (usersRepo.existsByPhone(request.getPhone()) && !user.getPhone().equals(request.getPhone())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }
        user.setAddress(request.getAddress());
        user.setGender(request.getGender());
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        usersRepo.save(user);
        return userMapper.requestToResponse(user);
    }

    @Override
    public Page<UserResponse> getAll(boolean active, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createAt").descending());
        return usersRepo.getALlByActive(active, pageable).map(userMapper::requestToResponse);
    }

    @Override
    public void deleteUser(UUID userId) {
        Users user = usersRepo.findByUserId(userId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        user.setActive(false);
        usersRepo.save(user);
    }

    @Override
    public UserResponse activeUser(UUID userId) {
        Users user = usersRepo.findByUserId(userId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        user.setActive(true);
        return userMapper.requestToResponse(usersRepo.save(user));
    }

    @Override
    public UserResponse getMyInfor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email == null) {
            throw new AppException(ErrorCode.INVALID_AUTHENCATION);
        }
        Users user = usersRepo.findByEmail(email);
        return userMapper.requestToResponse(user);
    }

    @Override
    public Users getUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email == null) {
            throw new AppException(ErrorCode.INVALID_AUTHENCATION);
        }
        Users user = usersRepo.findByEmail(email);
        return user;
    }

    @Override
    public UserResponse getUserById(UUID userId) {
        Users user = usersRepo.findByUserId(userId);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return userMapper.requestToResponse(user);
    }
    @Transactional
    @Override
    public void addRole(AddRemoveRoleRequest request) {
        Users user = usersRepo.findByUserId(request.getUserId());
        if(user == null)
        {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if(request.getRoleCode() != null && !request.getRoleCode().isEmpty())
        {
          request.getRoleCode().forEach(roleCode -> {
             Role role = roleRepo.findByRoleCode(roleCode);
              if(role == null)
              {
                  throw new AppException(ErrorCode.ROLE_NOT_FOUND);
              }
              user.getRolelist().add(role);
          });
        }
       usersRepo.save(user);

    }

    @Override
    public void removeRole(AddRemoveRoleRequest request) {
        Users user = usersRepo.findByUserId(request.getUserId());
        if(user == null)
        {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if(request.getRoleCode() != null && !request.getRoleCode().isEmpty())
        {
            request.getRoleCode().forEach(roleCode -> {
                Role role = roleRepo.findByRoleCode(roleCode);
                if(role == null)
                {
                    throw new AppException(ErrorCode.ROLE_NOT_FOUND);
                }
                user.getRolelist().remove(role);
            });
        }
        usersRepo.save(user);

    }
}
