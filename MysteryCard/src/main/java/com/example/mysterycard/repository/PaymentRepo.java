package com.example.mysterycard.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.mysterycard.entity.Payment;
import com.example.mysterycard.enums.StatusPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepo extends JpaRepository<Payment, UUID> {

    Payment findByTransactionRef(String transactionRef);

    Page<Payment> findByStatusPayment(StatusPayment statusPayment, Pageable pageable);
}
