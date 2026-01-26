package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.transaction.TransactionRequest;
import com.example.mysterycard.dto.request.transaction.UpdateTransactionStatusRequest;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.enums.StatusPayment;
import org.springframework.data.domain.Page;

public interface TransactionService {
    String createTransactionDepositeAndWithdraw(TransactionRequest transactionRequest);
    TransactionResponse updateTransactionStatus(UpdateTransactionStatusRequest request);
    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    TransactionResponse createRequestWithdraw(TransactionRequest transactionRequest);
    Page<TransactionResponse> getALlRequestWithdraw(StatusPayment statusPayment, int page, int size);
}
