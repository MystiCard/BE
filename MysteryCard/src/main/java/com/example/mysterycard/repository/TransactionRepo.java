package com.example.mysterycard.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.mysterycard.dto.request.transaction.TransactionRequest;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.Payment;
import com.example.mysterycard.entity.Wallet;
import com.example.mysterycard.entity.WalletTransaction;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.objenesis.instantiator.util.UnsafeUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TransactionRepo extends JpaRepository<WalletTransaction, UUID>, JpaSpecificationExecutor<WalletTransaction> {

    long countByCreateAtBetween(LocalDateTime createAtAfter, LocalDateTime createAtBefore);

    long countByCreateAtBetweenAndStatusTransaction(LocalDateTime createAtAfter, LocalDateTime createAtBefore, StatusPayment statusTransaction);

    @Query("""
      SELECT SUM(w.amount) from WalletTransaction  w  where w.createAt between :after and :before and w.statusTransaction = 'SUCCESS'
""")
    long sumAmount(LocalDateTime after, LocalDateTime before);

}
