package com.example.mysterycard.repository;

import com.example.mysterycard.dto.response.UserResponse;
import com.example.mysterycard.entity.Users;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UsersRepo extends JpaRepository<Users, String> {
    Users findByEmail(String email);

    boolean existsByEmail(String email);

    Users findByUserId(UUID userId);

    boolean existsByPhone(String phone);

    Page<Users> getALlByActive(boolean active, Pageable pageable);
}
