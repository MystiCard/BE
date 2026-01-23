package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.response.wallet.WalletResponse;
import com.example.mysterycard.entity.Wallet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletResponse entityToResponse(Wallet wallet);

}
