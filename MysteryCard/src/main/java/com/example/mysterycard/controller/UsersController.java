package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.user.AddRemoveRoleRequest;
import com.example.mysterycard.dto.request.user.UserRegisterRequest;
import com.example.mysterycard.dto.response.UserResponse;
import com.example.mysterycard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {
    private final UserService userService;
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestPart @Valid UserRegisterRequest request, @RequestPart(required = false) MultipartFile avatar) {
        return ResponseEntity.ok(ApiResponse.success(userService.createUser(request, avatar)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@RequestPart @Valid UserRegisterRequest request, @RequestPart(required = false) MultipartFile avatar, @PathVariable UUID id){
        return ResponseEntity.ok(ApiResponse.success(userService.updateUser(request, avatar, id)));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "true") boolean active

    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAll(active,page,size)));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Delete sucessfully user with id "+id));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/active/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userService.activeUser(id)));
    }
    @GetMapping("/my-infor")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfor() {
        return ResponseEntity.ok(ApiResponse.success(userService.getMyInfor()));
    }
    @GetMapping("{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserById(id)));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove-role")
    public ResponseEntity<ApiResponse<String>> removeRole(@RequestBody AddRemoveRoleRequest request) {
        userService.removeRole(request);
        return ResponseEntity.ok(ApiResponse.success("Remove sucessfully"));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/add-role")
    public ResponseEntity<ApiResponse<String>> addRole(@RequestBody AddRemoveRoleRequest request) {
        userService.addRole(request);
        return ResponseEntity.ok(ApiResponse.success("Add role sucessfully"));
    }
    @GetMapping("/shipper")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getListShipper(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(userService.getShipper(page,size)));
    }
}
