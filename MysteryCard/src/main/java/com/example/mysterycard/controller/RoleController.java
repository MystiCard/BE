package com.example.mysterycard.controller;

import com.cloudinary.Api;
import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.AddPermisionToRoleRequest;
import com.example.mysterycard.dto.request.RemovePermisionRequest;
import com.example.mysterycard.dto.request.RoleRequest;
import com.example.mysterycard.dto.response.RoleResponse;
import com.example.mysterycard.entity.Role;
import com.example.mysterycard.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RoleResponse>>> getALl(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "true") boolean active
    ) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getALLRole(active, page, size)));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<RoleResponse>> addRole(@RequestBody @Valid RoleRequest roleRequest) {
        return ResponseEntity.ok(ApiResponse.success(roleService.addRole(roleRequest)));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<String>> deleteRoleByCode(@PathVariable String code) {
        roleService.deleteRoleByCode(code);
        return ResponseEntity.ok(ApiResponse.success("Delete succesfully role with code " + code));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getRoleByCode(code)));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<Page<RoleResponse>>> getRoleByUserId(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "true") boolean active
    ) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getRoleByUserId(active,id,page,size)));
    }
    @DeleteMapping("/remove-permision")
    public ResponseEntity<ApiResponse<RoleResponse>> removePermision(@RequestBody  RemovePermisionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(roleService.removePermisionByRoleCodeAndPermisionCode(request)));
    }
    @PostMapping("/add-permision")
    public ResponseEntity<ApiResponse<RoleResponse>> removePermision(@RequestBody AddPermisionToRoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(roleService.addPermision(request)));
    }
    @PostMapping("/active/{code}")
    public ResponseEntity<ApiResponse<String>> activeRole(@PathVariable String code) {
        roleService.activeRole(code);
        return ResponseEntity.ok(ApiResponse.success("Active sucessfully role with code " + code));
    }
}
