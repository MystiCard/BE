package com.example.mysterycard.specification;

import com.example.mysterycard.entity.Wallet;
import com.example.mysterycard.entity.WalletTransaction;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TransactionSpecification {
    public static Specification<WalletTransaction> byStatus(StatusPayment statusPayment) {
        return (root,query,cb)
                -> statusPayment != null ? cb.equal(root.get("statusTransaction"), statusPayment) : null;

    }
    public static Specification<WalletTransaction> byTransactionType(TransactionType transactionType) {
        return (root,query,cb)
                -> transactionType != null ? cb.equal(root.get("transactionType"), transactionType) : null;
    }
    public static Specification<WalletTransaction> byPaymentId(UUID paymentId) {
        return (root,query,cb)
                -> paymentId != null ? cb.equal(root.get("payment").get("paymentId"), paymentId) : null;

    }
    public static Specification<WalletTransaction> byWallet(Wallet wallet) {
        return (root,query,cb)->
        { if(wallet == null)
        {
            return cb.conjunction();
        }
            return cb.or(
                    cb.equal(root.get("walletSend"),wallet),
                    cb.equal(root.get("walletReceive"),wallet)
            );
        };

    }
}
