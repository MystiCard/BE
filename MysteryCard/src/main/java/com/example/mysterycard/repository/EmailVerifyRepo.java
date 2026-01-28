package com.example.mysterycard.repository;

import com.example.mysterycard.entity.EmailVerify;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerifyRepo extends JpaRepository<EmailVerify, String> {
    EmailVerify findByEmailAndCode(String email, String code);

    EmailVerify findByEmail(String email);

    void deleteAllByEmail(String email);
}
