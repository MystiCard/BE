package com.example.mysterycard.service;

import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.dto.response.PaymentResponse;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.Payment;
import com.example.mysterycard.enums.StatusPayment;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.Map;
import java.util.UUID;

public interface PaymentService {
    String createPayment(Payment payment);
    boolean veryfySignature(HttpServletRequest request) ;
    Page<PaymentResponse> getAll(StatusPayment statusPayment, int page, int size);
    PaymentResponse getPayment(UUID id);
    PageResponse<PaymentResponse> getMyPayment(StatusPayment statusPayment, int page, int size);
    PaymentResponse getByTransactionId(UUID transactionId);
}
