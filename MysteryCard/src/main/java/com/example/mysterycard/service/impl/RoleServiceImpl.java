package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.AddPermisionToRoleRequest;
import com.example.mysterycard.dto.request.RemovePermisionRequest;
import com.example.mysterycard.dto.request.RoleRequest;
import com.example.mysterycard.dto.response.PermisionResponse;
import com.example.mysterycard.dto.response.RoleResponse;
import com.example.mysterycard.entity.Permision;
import com.example.mysterycard.entity.Role;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.PermisionMapper;
import com.example.mysterycard.mapper.RoleMapper;
import com.example.mysterycard.repository.PermisionRepo;
import com.example.mysterycard.repository.RoleRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;
    private final UsersRepo usersRepo;
    private final RoleMapper roleMapper;
    private final PermisionRepo permisionRepo;
    private final PermisionMapper permisionMapper;

    @Override
    public RoleResponse getRoleByCode(String roleCode) {
        Role role = roleRepo.findByRoleCode(roleCode);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        return roleMapper.enityToReponse(role);
    }

    @Override
    public Page<RoleResponse> getALLRole(boolean active, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return roleRepo.findAllByActive(active, pageable).map(roleMapper::enityToReponse);
    }

    @Override
    public void deleteRoleByCode(String roleCode) {
        Role role = roleRepo.findByRoleCode(roleCode);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        role.setActive(false);
        roleRepo.save(role);
    }

    @Override
    public Page<RoleResponse> getRoleByUserId(boolean active, UUID userId, int page, int size) {
        Users users = usersRepo.findByUserId(userId);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return roleRepo.findByActiveAndUsers(active, Set.of(users), pageable).map(roleMapper::enityToReponse);
    }

    @Transactional
    @Override
    public RoleResponse addRole(RoleRequest request) {
        if (roleRepo.existsRoleByRoleCode(request.getRoleCode())) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }
        Role role = roleMapper.requestToEntity(request);


        return roleMapper.enityToReponse(roleRepo.save(role));
    }
    @Transactional
    @Override
    public RoleResponse removePermisionByRoleCodeAndPermisionCode(RemovePermisionRequest request) {

        Role role = roleRepo.findByRoleCode(request.getRoleCode());
        if(role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        if(request.getPermisionCode() != null)
        {
            request.getPermisionCode().forEach(permisionCode -> {
                Permision permision = permisionRepo.findByPermisionCodeAndActive(permisionCode, true);
                if(permision == null) {
                    throw new AppException(ErrorCode.PERMISION_CODE_NOT_FOUND);
                }
                role.getPermisions().remove(permision);
            });

        }
        roleRepo.save(role);
        Set<PermisionResponse> permisionResponses = role.getPermisions().stream().map(permisionMapper::entityToResponse).collect(Collectors.toSet());
        RoleResponse response = roleMapper.enityToReponse(role);
        response.setPermisionResponse(permisionResponses);
        return response;
    }

    @Override
    public RoleResponse addPermision(AddPermisionToRoleRequest request) {
        Role role = roleRepo.findByRoleCode(request.getRoleCode());
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        if (request.getPermisionCode() != null) {
            request.getPermisionCode().forEach(permisionCode -> {
                Permision permision = permisionRepo.findByPermisionCodeAndActive(permisionCode, true);
                if (permision == null) {
                    throw new AppException(ErrorCode.PERMISION_CODE_NOT_FOUND);
                }
                role.getPermisions().add(permision);
            });
        }
        roleRepo.save(role);
        Set<PermisionResponse> permisionResponses = role.getPermisions().stream().map(permisionMapper::entityToResponse).collect(Collectors.toSet());
        RoleResponse response = roleMapper.enityToReponse(role);
        response.setPermisionResponse(permisionResponses);
        return response;
    }

    @Override
    public void activeRole(String roleCde) {
        Role role = roleRepo.findByRoleCode(roleCde);
        if (role == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        role.setActive(true);
        roleRepo.save(role);
    }


}
