package com.example.mysterycard.repository;

import com.example.mysterycard.dto.response.bankAccount.BankAccountResponse;
import com.example.mysterycard.entity.BankAccount;
import com.example.mysterycard.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BankAccountRepo extends JpaRepository<BankAccount, UUID> {
    List<BankAccount> findByUser(Users user);
    Page<BankAccount> findByUser_UserId(UUID userUserId, Pageable pageable);
    boolean existsByBankCodeAndAccountNumber(String bankCode, String accountNumber);
    List<BankAccount> findByUser_UserIdAndDefaultAccount(UUID userUserId, boolean defaultAccount);

    boolean existsByBankCodeAndAccountNumberAndUser(String bankCode, String accountNumber, Users user);
}
