package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.transaction.*;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.enums.StatusPayment;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TransactionService {
    String createTransactionDeposite(DepositeRequest request);
    TransactionResponse createRequestWithdraw(WithdrawRequest request);
    String addminApproveWithdraw(ApproveRequest request);
    TransactionResponse updateTransactionStatus(UpdateTransactionStatusRequest request);
    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    Page<TransactionResponse> getAllByStatus(SearchRequest request, int page, int size);
    TransactionResponse getById(UUID transactionId);
    TransactionResponse processTransaction(ProcessTransactionRequest request);
}
