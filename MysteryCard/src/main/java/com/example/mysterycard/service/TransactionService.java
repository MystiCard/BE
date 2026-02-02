package com.example.mysterycard.service;

import com.example.mysterycard.dto.request.TransactionReportRequest;
import com.example.mysterycard.dto.request.transaction.*;
import com.example.mysterycard.dto.response.TransactionReportResponse;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.enums.Status;
import com.example.mysterycard.enums.StatusPayment;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface TransactionService {
    String createTransactionDeposite(DepositeRequest request);
    TransactionResponse createRequestWithdraw(WithdrawRequest request);
    String addminApproveWithdraw(ApproveRequest request);
    TransactionResponse callBackDepositeAndWithdraw(UpdateTransactionStatusRequest request);
    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    Page<TransactionResponse> searchByPaymentId(SearchRequest request, UUID paymentId, int page, int size);
    TransactionResponse getById(UUID transactionId);
    TransactionResponse processTransaction(UUID transactionId);
    String payAgaint(UUID paymentId);
    Page<TransactionResponse> getMyTransaction(StatusPayment statusPayment, int page, int size);
    TransactionReportResponse report(TransactionReportRequest request);
}
