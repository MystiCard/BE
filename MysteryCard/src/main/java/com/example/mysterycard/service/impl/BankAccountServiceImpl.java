package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.bankAccount.BankAccountRequest;
import com.example.mysterycard.dto.request.bankAccount.ChangeDefaultRequest;
import com.example.mysterycard.dto.response.bankAccount.BankAccountResponse;
import com.example.mysterycard.entity.BankAccount;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.BankAccountMapper;
import com.example.mysterycard.repository.BankAccountRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountMapper bankAccountMapper;
    private final BankAccountRepo bankAccountRepo;
    private final UsersRepo usersRepo;
 @Transactional
    @Override
    public BankAccountResponse create(BankAccountRequest request, UUID userId) {
        Users users = usersRepo.findByUserId(userId);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        BankAccount bankAccount = bankAccountMapper.requestToEntity(request);
        if (bankAccountRepo.findByUser(users).isEmpty()) {
            bankAccount.setDefaultAccount(true);
        }
        if(bankAccountRepo.existsByBankCodeAndAccountNumberAndUser(request.getBankCode(), bankAccount.getAccountNumber(),users))
        {
            throw new AppException(ErrorCode.BANK_DUPLICATIN);
        }
        if(bankAccountRepo.existsByBankCodeAndAccountNumber(request.getBankCode(), bankAccount.getAccountNumber())) {
            throw new AppException(ErrorCode.BANK_CODE_EXISTED);
        }
        bankAccount.setUser(users);
        return bankAccountMapper.entityToResponse(bankAccountRepo.save(bankAccount));
    }
  @Transactional
    @Override
    public BankAccountResponse update(BankAccountRequest request, UUID bankCodeId) {
        BankAccount bankAccount = bankAccountRepo.findById(bankCodeId).orElseThrow(() -> new AppException((ErrorCode.BANK_ACCOUNT_NOT_FOUND)));
        if (bankAccountRepo.existsByBankCodeAndAccountNumber(request.getBankCode(),
                request.getAccountNumber()) && !bankAccount.getAccountNumber().equals(request.getAccountNumber()) || (!request.getBankCode().equals(bankAccount.getBankCode()))) {
            throw new AppException(ErrorCode.BANK_CODE_EXISTED);
        }
        BankAccount bk = bankAccountMapper.requestToEntity(request);
        bk.setCreatedAt(bankAccount.getCreatedAt());
        bk.setUser(bankAccount.getUser());
        bk.setBankAccountId(bankCodeId);
        bk.setDefaultAccount(bankAccount.isDefaultAccount());
        return bankAccountMapper.entityToResponse(bankAccountRepo.save(bk));
    }

    @Override
    public Page<BankAccountResponse> getByUserId(UUID uuid, int page,int size ) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return bankAccountRepo.findByUser_UserId(uuid, pageable).map(bankAccountMapper::entityToResponse);
    }

    @Override
    public BankAccountResponse getById(UUID bankID) {
        return bankAccountMapper.entityToResponse(bankAccountRepo.findById(bankID).orElseThrow(() -> new AppException(ErrorCode.BANK_ACCOUNT_NOT_FOUND)));
    }

    @Override
    public void deleteBankAccount(UUID bankID) {
        bankAccountRepo.deleteById(bankID);
    }
    @Transactional
    @Override
    public BankAccountResponse setDefault(ChangeDefaultRequest request) {
        Users users = usersRepo.findByUserId(request.getUserId());
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        List<BankAccount> oldDefault = bankAccountRepo.findByUser_UserIdAndDefaultAccount(request.getUserId(), true);
        if (oldDefault != null && !oldDefault.isEmpty()) {
            oldDefault.forEach(old -> {
                old.setDefaultAccount(false);
                ;
                bankAccountRepo.save(old);
            });
        }
        BankAccount bankAccount = bankAccountRepo.findById(request.getBankId()).orElseThrow(() -> new AppException(ErrorCode.BANK_ACCOUNT_NOT_FOUND));
        bankAccount.setDefaultAccount(true);
        return bankAccountMapper.entityToResponse(bankAccount);
    }
}
