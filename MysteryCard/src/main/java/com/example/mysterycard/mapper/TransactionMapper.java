package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.transaction.TransactionRequest;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.WalletTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = {BankAccountMapper.class})
public interface TransactionMapper {

    WalletTransaction requestToEnity(TransactionRequest request);
    @Mapping(target = "bankAccountResponse",source = "bankAccount")
    TransactionResponse entityToResponse(WalletTransaction walletTransaction);
}
