package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.transaction.TransactionRequest;
import com.example.mysterycard.dto.request.transaction.UpdateTransactionStatusRequest;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.BlindBoxRepo;
import com.example.mysterycard.mapper.PaymentMapper;
import com.example.mysterycard.mapper.TransactionMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.PaymentService;
import com.example.mysterycard.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// lay api cua wallet
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepo;
    private final TransactionMapper transactionMapper;
    private final PaymentMapper paymentMapper;
    private final PaymentService paymentService;
    private final OrderRepo orderRepo;
    private final BlindBoxRepo blindBoxRepo;
    private final BankAccountRepo bankAccountRepo;
    private final UsersRepo usersRepo;
    private final PaymentRepo paymentRepo;
    @Transactional
    @Override
    public String createTransactionDepositeAndWithdraw(TransactionRequest transactionRequest) {
        Wallet wallet = getWallet(transactionRequest.getUserId());
        WalletTransaction walletTransaction = transactionMapper.requestToEnity(transactionRequest);
        walletTransaction.setStatusTransaction(StatusPayment.PENDING);
        if(transactionRequest.getTransactionType().equals(TransactionType.DEPOSTIE))
        {
            walletTransaction.setWalletReceive(wallet);
        }
        else if( transactionRequest.getTransactionType().equals(TransactionType.WITHDRAW)){
            if(wallet.getBalance() < transactionRequest.getAmount())
            {
                throw new AppException(ErrorCode.CAN_NOT_WITHDRAW);
            }
            walletTransaction.setWalletSend(wallet);
        }
        Payment payment =  paymentMapper.requestToEntity(transactionRequest.getPaymentRequest());
        payment.setTransactionRef(UUID.randomUUID().toString());
        payment.setAmount(transactionRequest.getAmount());
        paymentRepo.save(payment);
        walletTransaction.setPayment(payment);
        transactionRepo.save(walletTransaction);
        return paymentService.createPayment(payment);
    }
    @Transactional
    @Override
    public TransactionResponse updateTransactionStatus(UpdateTransactionStatusRequest request) {
        WalletTransaction transaction = null;
        // rut tien/ nap tien thanh cong
        if(request.getTranferId() != null) {
            Payment payment = paymentRepo.findByTransactionRef(request.getTranferId().toString());
            if(payment != null && payment.getWalletTransaction() != null) {
                transaction = payment.getWalletTransaction();
            }else{
                throw new AppException(ErrorCode.TRANSACTION_NOT_FOUND);
            }
        }else{
            // thanh toan mua ban bang wallet
            transaction = transactionRepo.findById(request.getTransactionId()).orElseThrow(
                    ()-> new AppException(ErrorCode.TRANSACTION_NOT_FOUND)
            );
        }
        transaction.setStatusTransaction(request.getStatusPayment());
        transaction.setMessage(request.getMessage());
        if(request.getStatusPayment() == StatusPayment.SUCCESS) {
            Wallet receive = transaction.getWalletReceive();
            Wallet send = transaction.getWalletSend();
            if(receive != null ) {
                receive.setBalance(receive.getBalance() + transaction.getAmount());
            }
            if(send != null ) {
                send.setBalance(send.getBalance() - transaction.getAmount());
            }
        }
        return transactionMapper.entityToResponse(transactionRepo.save(transaction));
    }

    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        Order order =null;
        BlindBoxPurChase blindBoxPurChase;
        if(request.getOrderId() != null) {
             order = orderRepo.findById(request.getOrderId()).orElseThrow(
                    ()->  new AppException(ErrorCode.ORDER_NOT_FOUND)
            );

        } else if(request.getBlindboxPurchaseId() != null) {
             blindBoxPurChase = blindBoxRepo.findById(request.getBlindboxPurchaseId()).orElseThrow(()-<
                    new AppException(ErrorCode.BLIND_BOX_PURCHASE_NOT_FOUND)
            );
        }
        WalletTransaction  transaction = transactionMapper.requestToEnity(request);
        transaction.setStatusTransaction(StatusPayment.PENDING);
        if(order != null) {
            transaction.setOrder(order);
        }else if(blindBoxPurChase != null) {
            transaction.setBlindboxpurchase(blindBoxPurChase);
        }

        return transactionMapper.entityToResponse(transactionRepo.save(transaction));
    }
    @Transactional
    @Override
    public TransactionResponse createRequestWithdraw(TransactionRequest request) {
        Wallet wallet = getWallet(request.getUserId());
        BankAccount bankAccount = bankAccountRepo.findById(request.getBankId()).orElseThrow(
                ()-> new AppException(ErrorCode.BANK_ACCOUNT_NOT_FOUND)
        );
        WalletTransaction walletTransaction = transactionMapper.requestToEnity(request);
        walletTransaction.setStatusTransaction(StatusPayment.PENDING);
        walletTransaction.setBankAccount(bankAccount);
        walletTransaction.setWalletSend(wallet);
        return transactionMapper.entityToResponse(transactionRepo.save(walletTransaction));
    }

    @Override
    public Page<TransactionResponse> getALlRequestWithdraw(StatusPayment statusPayment, int page, int size) {
        Pageable pageable = PageRequest.of(page-1,size, Sort.by("createAt").descending());
        return transactionRepo.findByStatusTransaction(statusPayment,pageable).map(transactionMapper::entityToResponse);
    }

    public Wallet getWallet(UUID userId) {
        Users users = usersRepo.findByUserId(userId);
        if(users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if(!users.isActive())
        {
            throw  new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        return users.getWallet();
    }

}
