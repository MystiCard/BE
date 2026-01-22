package com.example.mysterycard.repository;

import com.example.mysterycard.dto.response.RoleResponse;
import com.example.mysterycard.entity.Role;
import com.example.mysterycard.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RoleRepo extends JpaRepository<Role, String> {
    Role findByRoleCode(String roleCode);

    Page<Role> findAllByActive(boolean active, Pageable pageable);

    Page<Role> findByActiveAndUsers(boolean active, Set<Users> users, Pageable pageable);

    boolean existsRoleByRoleCode(String roleCode);
}
