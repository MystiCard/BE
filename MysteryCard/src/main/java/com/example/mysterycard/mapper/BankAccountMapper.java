package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.bankAccount.BankAccountRequest;
import com.example.mysterycard.dto.response.bankAccount.BankAccountResponse;
import com.example.mysterycard.entity.BankAccount;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {
    BankAccountResponse entityToResponse(BankAccount request);
    BankAccount requestToEntity(BankAccountRequest request);
}
