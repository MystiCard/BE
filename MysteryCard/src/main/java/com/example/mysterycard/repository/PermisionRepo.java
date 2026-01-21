package com.example.mysterycard.repository;

import com.example.mysterycard.dto.response.PermisionResponse;
import com.example.mysterycard.entity.Permision;
import com.example.mysterycard.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PermisionRepo extends JpaRepository<Permision, String> {
    Page<Permision> findAllByRoles(Set<Role> roles, Pageable pageable);
}
