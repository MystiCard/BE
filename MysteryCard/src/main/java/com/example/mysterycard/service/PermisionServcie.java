package com.example.mysterycard.service;

import com.example.mysterycard.dto.response.PermisionResponse;
import com.example.mysterycard.entity.Permision;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PermisionServcie {
    PermisionResponse addPermision(Permision permision);
    Page<PermisionResponse> getByRoleCode(String roleCode,int page, int size,boolean active);
    void deletePermision(String permisionCode);
    Page<PermisionResponse> getAll(int page , int size,boolean active);
}
