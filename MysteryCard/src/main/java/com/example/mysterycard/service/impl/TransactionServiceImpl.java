package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.TransactionReportRequest;
import com.example.mysterycard.dto.request.transaction.*;
import com.example.mysterycard.dto.response.TransactionReportResponse;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.OrderStatus;
import com.example.mysterycard.enums.ShippingStatus;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.enums.TransactionType;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.PaymentMapper;
import com.example.mysterycard.mapper.SumariesMapper;
import com.example.mysterycard.mapper.TransactionMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.PaymentService;
import com.example.mysterycard.service.TransactionService;
import com.example.mysterycard.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.method.P;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepo;
    private final TransactionMapper transactionMapper;
    private final PaymentService paymentService;
    private final OrderRepo orderRepo;
    private final BankAccountRepo bankAccountRepo;
    private final UsersRepo usersRepo;
    private final PaymentRepo paymentRepo;
    private final SumariesRepository sumariesRepository;
    private final SumariesMapper sumariesMapper;

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
                .content("Top up for wallet")
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
                .content("Withdraw money from wallet")
                .build();
        paymentRepo.save(payment);
        walletTransaction.setPayment(payment);
        walletTransaction.setTransactionType(TransactionType.WITHDRAW);
        transactionRepo.save(walletTransaction);
        return paymentService.createPayment(payment);
    }
    @Transactional
    @Override
    public TransactionResponse callBackDepositeAndWithdraw(UpdateTransactionStatusRequest request) {
        WalletTransaction transaction = null;
        // rut tien/ nap tien thanh cong
            Payment payment = paymentRepo.findByTransactionRef(request.getTranferId().toString());
            if (payment == null ) {
                throw new AppException(ErrorCode.TRANSACTION_NOT_FOUND);
            }
        transaction = payment.getWalletTransactions().getLast();
        transaction.setStatusTransaction(request.getStatusPayment());
        payment.setStatusPayment(request.getStatusPayment());
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
        String message = "Transaction for Blind Box";
        if (admin == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Long amount = null;
        Wallet seller =  admin.getWallet();
        transaction.setStatusTransaction(StatusPayment.SUCCESS);
        if (request.getOrderId() != null && request.getSellerId() != null) {
            Order order = orderRepo.findById(request.getOrderId()).orElseThrow(
                    () -> new AppException(ErrorCode.ORDER_NOT_FOUND)
            );
            amount = order.getTotalAmount();
            transaction.setOrder(order);
            if(order.getBlindBox() == null)
            {
                message = "Transaction for buy Card";
                 seller = getWallet(request.getSellerId());
                transaction.setStatusTransaction(StatusPayment.ESCROWED);
            }
        }
        transaction.setMessage(message);
        transaction.setWalletReceive(seller);
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
    public Page<TransactionResponse> searchByPaymentId(SearchRequest request, UUID paymentId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createAt").descending());
        Specification<WalletTransaction> spe = Specification.allOf(
                TransactionSpecification.byTransactionType(request.getTransactionType()),
                TransactionSpecification.byStatus(request.getStatusPayment()),
                TransactionSpecification.byPaymentId(paymentId)
        );
        return transactionRepo.findAll(spe,pageable).map(transactionMapper::entityToResponse);
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
        Wallet seller = transaction.getWalletReceive();
        if (order != null) {
            transaction.setStatusTransaction(StatusPayment.REFUNDED);
            OrderStatus orderStatus = order.getStatus();
            ShippingStatus shippingStatus = shipment.getShipmentStatus();
            Long price = (transaction.getAmount() - shipment.getShipmentFee());
            if (orderStatus.equals(OrderStatus.COMPLETED) && transaction.getStatusTransaction().equals(StatusPayment.ESCROWED)) {
                seller.setBalance(seller.getBalance() + price );
                adminWallet.setBalance(adminWallet.getBalance() - price);
                transaction.setStatusTransaction(StatusPayment.RELEASED);
            } else if (orderStatus.equals(OrderStatus.CANCELLED) ) {

                if(shippingStatus.equals(ShippingStatus.PENDING) && order.getBlindBox() == null) {
                    buyer.setBalance(buyer.getBalance() + transaction.getAmount());
                    adminWallet.setBalance(adminWallet.getBalance() - transaction.getAmount());

                }else if(shippingStatus.equals(ShippingStatus.RETURNED)) {
                    Long refund_money = transaction.getAmount() - shipment.getShipmentFee();
                    buyer.setBalance(buyer.getBalance() + refund_money);
                    adminWallet.setBalance(adminWallet.getBalance() - refund_money);
                }else if(shippingStatus.equals(ShippingStatus.LOST)) {
                    buyer.setBalance(buyer.getBalance() + transaction.getAmount());
                    adminWallet.setBalance(adminWallet.getBalance() - transaction.getAmount());
                    seller.setBalance(seller.getBalance() + price);
                    adminWallet.setBalance(adminWallet.getBalance() - price);

                }


            }
        }
        usersRepo.save(admin);
        return transactionMapper.entityToResponse(transactionRepo.save(transaction));
    }

    @Override
    public String payAgaint(UUID paymentId) {
        Payment payment = paymentRepo.findById(paymentId).orElseThrow(
                ()-> new AppException(ErrorCode.PAYMENT_NOT_FOUND)
        );
        WalletTransaction transaction = payment.getWalletTransactions().getLast();
        if(transaction != null && transaction.getStatusTransaction().equals(StatusPayment.FAILED))
        {
            WalletTransaction newTransaction = WalletTransaction.builder()
                    .walletSend(transaction.getWalletSend())
                    .walletReceive(transaction.getWalletReceive())
                    .transactionType(transaction.getTransactionType())
                    .statusTransaction(StatusPayment.PENDING)
                    .bankAccount(transaction.getBankAccount())
                    .amount(transaction.getAmount())
                    .build();
            payment.setTransactionRef(UUID.randomUUID().toString());
            newTransaction.setPayment(payment);
            transactionRepo.save(newTransaction);
            return paymentService.createPayment(payment);
        }
       return "";
    }
    @Override
    public Page<TransactionResponse> getMyTransaction(StatusPayment statusPayment, int page, int size) {
      String email = SecurityContextHolder.getContext().getAuthentication().getName();
      Users user = usersRepo.findByEmail(email);
      if(user == null) {
          throw new AppException(ErrorCode.USER_NOT_FOUND);
      }
      Wallet wallet = getWallet(user.getUserId());
        Pageable pageable = PageRequest.of(page-1, size,Sort.by("createAt").descending());
        Specification<WalletTransaction> spe = Specification.allOf(TransactionSpecification.byStatus(statusPayment),TransactionSpecification.byWallet(wallet));
        return transactionRepo.findAll(spe,pageable).map(transactionMapper::entityToResponse);
    }

    @Override
    public TransactionReportResponse report(TransactionReportRequest request) {
        TransactionReportResponse response  = TransactionReportResponse.builder().build();
        List<Summaries> summaries = sumariesRepository.findByLocalDateBetween(request.getFrom(),request.getTo());
        if(request.getTo().equals(LocalDate.now()))
        {
            LocalDateTime start = LocalDate.now().atStartOfDay();
            LocalDateTime end = LocalDateTime.now();
            Summaries s = Summaries.builder()
                    .localDate(LocalDate.now())
                    .totalPayment(transactionRepo.countByCreateAtBetween(start,end))
                    .error(transactionRepo.countByCreateAtBetweenAndStatusTransaction(start,end, StatusPayment.FAILED))
                    .success(transactionRepo.countByCreateAtBetweenAndStatusTransaction(start,end, StatusPayment.SUCCESS))
                    .totalAmount(transactionRepo.sumAmount(start,end))
                    .build();
            summaries.add(s);
        }
        if(summaries != null)
        {
            summaries.forEach((s)->{
                response.setTotalError(response.getTotalError() + s.getError());
                response.setTotalSuccess(response.getTotalSuccess() + s.getSuccess());
                response.setTotalPayment(response.getTotalPayment()+ s.getTotalPayment());
                response.setTotalAmount(response.getTotalAmount()+ s.getTotalAmount());

            });
            response.setTotalPending(response.getTotalPayment() -(response.getTotalPending()+ response.getTotalError()));
        }
        response.setData(summaries.stream().map(sumariesMapper::entityToResponse).toList());
        return response;

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
