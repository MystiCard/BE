package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.response.wallet.WalletResponse;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.entity.Wallet;
import com.example.mysterycard.enums.WalletStatus;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.WalletMapper;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.repository.WalletRepo;
import com.example.mysterycard.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    private final WalletRepo walletRepo;
    private final WalletMapper walletMapper;
    private final UsersRepo usersRepo;
    @Override
    public WalletResponse createWallet(Users users) {
        Wallet wallet = new Wallet();
       wallet.setWalletStatus(WalletStatus.ACTIVE);
       wallet.setBalance(0.0);
       wallet.setOnwer(users);
        return walletMapper.entityToResponse(walletRepo.save(wallet));
    }

    @Override
    public WalletResponse activeWallet(UUID uuid) {
        Users user = usersRepo.findByUserId(uuid);
        if(user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        user.getWallet().setWalletStatus(WalletStatus.ACTIVE);
        usersRepo.save(user);
        return walletMapper.entityToResponse(user.getWallet());
    }

    @Override
    public WalletResponse deActiveWallet(UUID uuid) {
        Users user = usersRepo.findByUserId(uuid);
        if(user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        user.getWallet().setWalletStatus(WalletStatus.SUSPENDED);
        usersRepo.save(user);
        return walletMapper.entityToResponse(user.getWallet());
    }
}
