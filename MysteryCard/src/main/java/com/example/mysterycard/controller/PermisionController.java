package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.response.PermisionResponse;
import com.example.mysterycard.entity.Permision;
import com.example.mysterycard.service.PermisionServcie;
import com.example.mysterycard.service.impl.PermisionServiceImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/permisions")
@RestController
@RequiredArgsConstructor
public class PermisionController {
    private final PermisionServcie permisionServcie;
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PermisionResponse>>> getAll(
            @RequestParam(required = false,defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(permisionServcie.getAll(page,size)));
    }
    @GetMapping("/{roleCode}")
    public ResponseEntity<ApiResponse<Page<PermisionResponse>>> getPermisionByRoleCode(
            @PathVariable String roleCode,
            @RequestParam(required = false,defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size

    ) {
        return ResponseEntity.ok(ApiResponse.success(permisionServcie.getByRoleCode(roleCode,page,size)));
    }
    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse> deletePermisionByCode(@PathVariable String code) {
        permisionServcie.deletePermision(code);
        return ResponseEntity.ok(ApiResponse.success("Delete succesfully permision with code "+code));
    }
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addPermision(@RequestBody Permision permision) {
        return ResponseEntity.ok(ApiResponse.success("Add Succesfully permision",permisionServcie.addPermision(permision)));
    }
}
