package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.TransactionReportRequest;
import com.example.mysterycard.dto.request.transaction.*;
import com.example.mysterycard.dto.response.TransactionReportResponse;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/transactions")
@RestController
public class TransactionController {
    private final TransactionService transactionService;
    @PostMapping("/deposite")
    public ResponseEntity<ApiResponse<String>> createTransaction(@RequestBody @Valid DepositeRequest request) {
        return  ResponseEntity.ok(ApiResponse.success(transactionService.createTransactionDeposite(request)));
    }
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(@RequestBody @Valid UpdateTransactionStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.callBackDepositeAndWithdraw(request)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getById(id)));
    }

    @PostMapping("/request-withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> requestWithdraw(@RequestBody @Valid WithdrawRequest transactionRequest) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.createRequestWithdraw(transactionRequest)));
    }
    @PostMapping("/pay-with-wallet")
    public ResponseEntity<ApiResponse<TransactionResponse>> payWithWallet(@RequestBody @Valid TransactionRequest transactionRequest) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.createTransaction(transactionRequest)));
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<String>> approve(@RequestBody @Valid ApproveRequest approveRequest) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.addminApproveWithdraw(approveRequest)));
    }
    @PostMapping("/pay-againt/{paymentId}")
    @Operation(summary = "Pay againt for payment by PaymentId")
    public ResponseEntity<ApiResponse<TransactionResponse>> payAgain(@PathVariable UUID paymentId) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.payAgaint(paymentId)));
    }
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getMyTransaction(
            @RequestParam(required = false) StatusPayment statusPayment,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    )
    {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getMyTransaction(statusPayment,page,size)));
    }
    @PostMapping("/{paymentsId}/payments")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getAllTransactions(
            @PathVariable UUID paymentsId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestBody(required = false) SearchRequest searchRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.searchByPaymentId(searchRequest,paymentsId,page,size)));
    }
    @PostMapping("/report")
    public ResponseEntity<ApiResponse<TransactionReportResponse>> report(
            @RequestBody TransactionReportRequest request
            ){
        return ResponseEntity.ok(ApiResponse.success(transactionService.report(request)));
    }

}
