package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PrivilegeRepo extends JpaRepository<Privilege, String> {
}
