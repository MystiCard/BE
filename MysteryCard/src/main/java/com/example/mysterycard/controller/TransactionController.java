package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.transaction.TransactionRequest;
import com.example.mysterycard.dto.request.transaction.UpdateTransactionStatusRequest;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/transactions")
@RestController
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createTransaction(@RequestBody @Valid TransactionRequest request) {
        return  ResponseEntity.ok(ApiResponse.success(transactionService.createTransactionDepositeAndWithdraw(request)));
    }
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(@RequestBody UpdateTransactionStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.updateTransactionStatus(request)));
    }

}
