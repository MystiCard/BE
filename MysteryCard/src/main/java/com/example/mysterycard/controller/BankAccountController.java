package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.bankAccount.BankAccountRequest;
import com.example.mysterycard.dto.request.bankAccount.ChangeDefaultRequest;
import com.example.mysterycard.dto.response.bankAccount.BankAccountResponse;
import com.example.mysterycard.repository.BankAccountRepo;
import com.example.mysterycard.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/bank-account")
@RequiredArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<ApiResponse<BankAccountResponse>> createAcount(
            @RequestBody BankAccountRequest bankAccountRequest,
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.create(bankAccountRequest, userId)));
    }

    @PutMapping("/update/{bankId}")
    public ResponseEntity<ApiResponse<BankAccountResponse>> update(
            @RequestBody BankAccountRequest request,
            @PathVariable UUID bankId) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.update(request, bankId)));
    }

    @GetMapping("/bank-user/{userId}")
    public ResponseEntity<ApiResponse<Page<BankAccountResponse>>> getByUserId(
            @PathVariable UUID userId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getByUserId(userId, page, size)));
    }

    @GetMapping("/{bankID}")
    public ResponseEntity<ApiResponse<BankAccountResponse>> getById(@PathVariable UUID bankID) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getById(bankID)));
    }

    @DeleteMapping("/{bankId}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable UUID bankId) {
        bankAccountService.deleteBankAccount(bankId);
        return ResponseEntity.ok(ApiResponse.success("Deleted bank account sucessfully"));
    }

    @PutMapping("/default")
    public ResponseEntity<ApiResponse<BankAccountResponse>> setDefault(@RequestBody ChangeDefaultRequest request) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.setDefault(request)));
    }


}
