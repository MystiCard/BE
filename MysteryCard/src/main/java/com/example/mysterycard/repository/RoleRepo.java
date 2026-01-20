package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, String> {
    Role findByRoleCode(String roleCode);
}
