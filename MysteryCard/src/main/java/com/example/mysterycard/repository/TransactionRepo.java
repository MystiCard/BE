package com.example.mysterycard.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.mysterycard.dto.request.transaction.TransactionRequest;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.WalletTransaction;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;

import java.util.UUID;

public interface TransactionRepo extends JpaRepository<WalletTransaction, UUID> {
    Page<WalletTransaction> findByStatusTransactionAndTransactionType(StatusPayment statusTransaction, TransactionType transactionType, Pageable pageable);

    Page<WalletTransaction> findByStatusTransaction(StatusPayment statusTransaction, Pageable pageable);

    Page<WalletTransaction> findByTransactionType(TransactionType transactionType, Pageable pageable);
}
