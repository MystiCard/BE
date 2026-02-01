package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.transaction.*;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.OrderStatus;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
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

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepo;
    private final TransactionMapper transactionMapper;
    private final PaymentMapper paymentMapper;
    private final PaymentService paymentService;
    private final OrderRepo orderRepo;
    private final BankAccountRepo bankAccountRepo;
    private final UsersRepo usersRepo;
    private final PaymentRepo paymentRepo;

    @Transactional
    @Override
    public String createTransactionDeposite(DepositeRequest request) {
        Wallet wallet = getWallet(request.getUserId());
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .amount(request.getAmount())
                .transactionType(TransactionType.DEPOSTIE)
                .statusTransaction(StatusPayment.PENDING)
                .walletReceive(wallet)
                .build();
        Payment payment = Payment.builder()
                .provider(request.getProvider())
                .transactionRef(UUID.randomUUID().toString())
                .amount(request.getAmount())
                .build();
        paymentRepo.save(payment);
        walletTransaction.setPayment(payment);
        transactionRepo.save(walletTransaction);
        return paymentService.createPayment(payment);
    }

    @Transactional
    @Override
    public TransactionResponse createRequestWithdraw(WithdrawRequest request) {
        Wallet wallet = getWallet(request.getUserId());
        BankAccount bankAccount = bankAccountRepo.findById(request.getBankId()).orElseThrow(
                () -> new AppException(ErrorCode.BANK_ACCOUNT_NOT_FOUND)
        );
        if (request.getAmount() > wallet.getBalance()) {
            throw new AppException(ErrorCode.CAN_NOT_WITHDRAW);
        }
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .amount(request.getAmount())
                .walletSend(wallet)
                .bankAccount(bankAccount)
                .statusTransaction(StatusPayment.PENDING)
                .transactionType(TransactionType.REQUEST_WITHDRAW)
                .build();
        return transactionMapper.entityToResponse(transactionRepo.save(walletTransaction));
    }

    @Override
    public String addminApproveWithdraw(ApproveRequest request) {
        WalletTransaction walletTransaction = transactionRepo.findById(request.getTransactionId()).orElseThrow(
                () -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND)
        );
        Payment payment = Payment.builder()
                .provider(request.getProvider())
                .transactionRef(UUID.randomUUID().toString())
                .amount(walletTransaction.getAmount())
                .build();
        paymentRepo.save(payment);
        walletTransaction.setPayment(payment);
        walletTransaction.setTransactionType(TransactionType.WITHDRAW);
        transactionRepo.save(walletTransaction);
        return paymentService.createPayment(payment);
    }


    @Transactional
    @Override
    public TransactionResponse updateTransactionStatus(UpdateTransactionStatusRequest request) {
        WalletTransaction transaction = null;
        // rut tien/ nap tien thanh cong
        if (request.getTranferId() != null) {
            Payment payment = paymentRepo.findByTransactionRef(request.getTranferId().toString());
            if (payment != null ) {

                List<WalletTransaction> walletTransactionList = transactionRepo.findByPaymentOrderByCreateAtDesc(payment);
                if(walletTransactionList == null && walletTransactionList.isEmpty())
                {
                    throw new AppException(ErrorCode.TRANSACTION_NOT_FOUND);
                }
                transaction = walletTransactionList.get(0);
            } else {
                throw new AppException(ErrorCode.TRANSACTION_NOT_FOUND);
            }
        } else {
            // thanh toan mua ban bang wallet
            transaction = transactionRepo.findById(request.getTransactionId()).orElseThrow(
                    () -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND)
            );
        }
        transaction.setStatusTransaction(request.getStatusPayment());
        transaction.setMessage(request.getMessage());
        if (request.getStatusPayment() == StatusPayment.SUCCESS) {
            Wallet receive = transaction.getWalletReceive();
            Wallet send = transaction.getWalletSend();
            if (receive != null) {
                receive.setBalance(receive.getBalance() + transaction.getAmount());
            }
            if (send != null) {
                send.setBalance(send.getBalance() - transaction.getAmount());
            }
        }
        return transactionMapper.entityToResponse(transactionRepo.save(transaction));
    }

    @Transactional
    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        Wallet buyer = getWallet(request.getBuyerId());
        WalletTransaction transaction = transactionMapper.requestToEnity(request);
        Users admin = usersRepo.findByEmail("admin@mysterycard.com");
        if (admin == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Long amount = null;

        if (request.getOrderId() != null && request.getSellerId() != null) {
            Order order = orderRepo.findById(request.getOrderId()).orElseThrow(
                    () -> new AppException(ErrorCode.ORDER_NOT_FOUND)
            );
            Wallet seller = getWallet(request.getSellerId());
            amount = order.getTotalAmount();
            transaction.setWalletReceive(seller);
            transaction.setOrder(order);
            transaction.setStatusTransaction(StatusPayment.ESCROWED);
        }
        transaction.setWalletSend(buyer);
        transaction.setAmount(amount);
        if (buyer.getBalance() < amount) {
            transaction.setStatusTransaction(StatusPayment.FAILED);
            transaction.setMessage(ErrorCode.CAN_NOT_TRANSACTION.getMessage());
        } else {
            admin.getWallet().setBalance(admin.getWallet().getBalance() + amount);
            usersRepo.save(admin);
            buyer.setBalance(buyer.getBalance() - amount);
        }
        return transactionMapper.entityToResponse(transactionRepo.save(transaction));
    }

    @Override
    public Page<TransactionResponse> getAllByStatus(SearchRequest request, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createAt").descending());
        if (request == null) {
            return transactionRepo.findAll(pageable).map(transactionMapper::entityToResponse);
        }
        if (request.getTransactionType() != null && request.getStatusPayment() != null) {
            return transactionRepo.findByStatusTransactionAndTransactionType(request.getStatusPayment(), request.getTransactionType(), pageable).map(transactionMapper::entityToResponse);
        }
        if (request.getStatusPayment() != null) {
            return transactionRepo.findByStatusTransaction(request.getStatusPayment(), pageable).map(transactionMapper::entityToResponse);
        }
        return transactionRepo.findByTransactionType(request.getTransactionType(), pageable).map(transactionMapper::entityToResponse);
    }

    @Transactional
    @Override
    public TransactionResponse getById(UUID transactionId) {
        WalletTransaction transaction = transactionRepo.findById(transactionId).orElseThrow(
                () -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND)
        );
        return transactionMapper.entityToResponse(transaction);
    }
    @Transactional
    @Override
    public TransactionResponse processTransaction(UUID transactionId) {
        WalletTransaction transaction = transactionRepo.findById(transactionId).orElseThrow(
                () -> new AppException(ErrorCode.TRANSACTION_NOT_FOUND)
        );
        Users admin = usersRepo.findByEmail("admin@mysterycard.com");

        if (admin == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Wallet adminWallet = admin.getWallet();
        Order order = transaction.getOrder();
       // xem lai
        Shipment shipment =  order.getShipment().getLast();
        Wallet buyer = transaction.getWalletSend();
        if (order != null) {
            transaction.setStatusTransaction(StatusPayment.REFUNDED);
            OrderStatus orderStatus = order.getStatus();
            ShippingStatus shippingStatus = shipment.getShipmentStatus();
            Long price = (transaction.getAmount() - shipment.getShipmentFee());
            if (orderStatus.equals(OrderStatus.COMPLETED)) {
                Wallet seller = transaction.getWalletReceive();
                seller.setBalance(seller.getBalance() + price );
                adminWallet.setBalance(adminWallet.getBalance() - price);
                transaction.setStatusTransaction(StatusPayment.RELEASED);
            } else if (orderStatus.equals(OrderStatus.CANCELLED) ) {


                if(shippingStatus.equals(ShippingStatus.PENDING)) {

                    buyer.setBalance(buyer.getBalance() + transaction.getAmount());
                    adminWallet.setBalance(adminWallet.getBalance() - transaction.getAmount());

                }else if(shippingStatus.equals(ShippingStatus.RETURNED)) {
                    Long refund_money = transaction.getAmount() - shipment.getShipmentFee();
                    buyer.setBalance(buyer.getBalance() + refund_money);
                    adminWallet.setBalance(adminWallet.getBalance() - refund_money);
                }


            }else if(shippingStatus.equals(ShippingStatus.LOST)) {
                buyer.setBalance(buyer.getBalance() + transaction.getAmount());
                adminWallet.setBalance(adminWallet.getBalance() - transaction.getAmount());
                Wallet seller = transaction.getWalletReceive();
                seller.setBalance(seller.getBalance() + price);
                adminWallet.setBalance(adminWallet.getBalance() - price);

            }
        }
        usersRepo.save(admin);
        return transactionMapper.entityToResponse(transactionRepo.save(transaction));
    }

    public Wallet getWallet(UUID userId) {
        Users users = usersRepo.findByUserId(userId);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (!users.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        return users.getWallet();
    }

}
