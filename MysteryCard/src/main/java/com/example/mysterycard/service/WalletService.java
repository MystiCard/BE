package com.example.mysterycard.service;

import com.example.mysterycard.dto.response.wallet.WalletResponse;
import com.example.mysterycard.entity.Users;

import java.util.UUID;

public interface WalletService {
    WalletResponse createWallet(Users users);
    WalletResponse activeWallet(UUID uuid);
    WalletResponse deActiveWallet(UUID uuid);
}
