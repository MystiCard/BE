package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.response.wallet.WalletResponse;
import com.example.mysterycard.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/active/{userId}")
    public ResponseEntity<ApiResponse<WalletResponse>> activeWallet(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(walletService.activeWallet(userId)));
    }
    @DeleteMapping("/deActive/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WalletResponse>> deActiveWallet(@PathVariable UUID userId) {
        return ResponseEntity.ok(ApiResponse.success(walletService.deActiveWallet(userId)));
    }
}
