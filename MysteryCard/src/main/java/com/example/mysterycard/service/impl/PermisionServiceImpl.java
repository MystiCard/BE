package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.response.PermisionResponse;
import com.example.mysterycard.entity.Permision;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.PermisionMapper;
import com.example.mysterycard.repository.PermisionRepo;
import com.example.mysterycard.repository.RoleRepo;
import com.example.mysterycard.service.PermisionServcie;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermisionServiceImpl implements PermisionServcie {
    private final PermisionRepo permisionRepo;
    private final RoleRepo roleRepo;
    private final PermisionMapper permisionMapper;
    @Override
    public PermisionResponse addPermision(Permision permision) {

        if(permisionRepo.existsById(permision.getPermisionCode())){
            throw new AppException(ErrorCode.PERMISION_CODE_EXISTED);
        }
        return permisionMapper.entityToResponse(permisionRepo.save(permision));
    }
    @Override
    public Page<PermisionResponse> getByRoleCode(String roleCode,int page, int  size,boolean active) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());
        return  permisionRepo.findByRolesAndActive(Set.of(roleRepo.findByRoleCode(roleCode)),active,pageable).map(permisionMapper::entityToResponse);
    }
    @Override
    public void deletePermision(String permisionCode) {
        if(!permisionRepo.existsById(permisionCode)){
            throw new AppException(ErrorCode.PERMISION_CODE_NOT_FOUND);
        }
        Permision permision = permisionRepo.findById(permisionCode).get();
        permision.setActive(false);
        permisionRepo.save(permision);
    }
    @Override
    public Page<PermisionResponse> getAll(int page, int size,boolean active) {
        Pageable pageable = PageRequest.of(page-1, size, Sort.by("createdAt").descending());
        return permisionRepo.findByActive(active,pageable).map(permisionMapper::entityToResponse);
    }
}
