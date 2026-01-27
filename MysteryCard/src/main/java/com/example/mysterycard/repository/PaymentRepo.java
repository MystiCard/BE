package com.example.mysterycard.repository;

import com.example.mysterycard.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepo extends JpaRepository<Payment, UUID> {

    Payment findByTransactionRef(String transactionRef);
}
