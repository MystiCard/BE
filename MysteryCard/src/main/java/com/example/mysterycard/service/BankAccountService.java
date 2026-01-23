package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.bankAccount.BankAccountRequest;
import com.example.mysterycard.dto.request.bankAccount.ChangeDefaultRequest;
import com.example.mysterycard.dto.response.bankAccount.BankAccountResponse;
import com.example.mysterycard.entity.Users;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface BankAccountService {
    BankAccountResponse create(BankAccountRequest request, UUID userId);
    BankAccountResponse update(BankAccountRequest request, UUID bankCodeId);
    Page<BankAccountResponse> getByUserId(UUID uuid,int page,  int size);
    BankAccountResponse getById(UUID bankID);
    void deleteBankAccount(UUID bankID);
    BankAccountResponse setDefault(ChangeDefaultRequest request);
}
