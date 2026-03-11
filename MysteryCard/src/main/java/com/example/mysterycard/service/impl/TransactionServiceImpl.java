package com.example.mysterycard.service.impl;

import com.example.mysterycard.dto.request.TransactionReportRequest;
import com.example.mysterycard.dto.request.UpdateShipmentRequest;
import com.example.mysterycard.dto.request.transaction.*;
import com.example.mysterycard.dto.response.TransactionReportResponse;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.*;
import com.example.mysterycard.enums.*;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;import com.example.mysterycard.mapper.SumariesMapper;
import com.example.mysterycard.mapper.TransactionMapper;
import com.example.mysterycard.repository.*;
import com.example.mysterycard.service.NotificationService;
import com.example.mysterycard.service.PaymentService;
import com.example.mysterycard.service.ShipmentService;
import com.example.mysterycard.service.TransactionService;
import com.example.mysterycard.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final ShipmentService shipmentService;
    private final ListSellerRepo listSellerRepo;
    private final WalletRepo walletRepo;
    private final ReturnRequestRepo returnRequestRepo;
    private final NotificationService notificationService;
    @Value("${admin.email}")
    private String adminEmail;

    @Transactional
    @Override
    public String createTransactionDeposite(DepositeRequest request) {
        Wallet wallet = getWallet(request.getUserId());
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .amount(Double.valueOf(request.getAmount()))
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(email);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Wallet wallet =  getWallet(users.getUserId());
        BankAccount bankAccount = bankAccountRepo.findById(request.getBankId()).orElseThrow(
                () -> new AppException(ErrorCode.BANK_ACCOUNT_NOT_FOUND)
        );
        if (request.getAmount() > wallet.getBalance()) {
            throw new AppException(ErrorCode.CAN_NOT_WITHDRAW);
        }
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .amount(Double.valueOf(request.getAmount()))
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
                .amount(Math.round(walletTransaction.getAmount()))
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
        if (payment == null) {
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
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users buyerUser = usersRepo.findByEmail(email);
        if (buyerUser == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Wallet buyer = getWallet(buyerUser.getUserId());

        Users admin = usersRepo.findByEmail(adminEmail);
        if (admin == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Order order = orderRepo.findById(request.getOrderId()).orElseThrow(
                () -> new AppException(ErrorCode.ORDER_NOT_FOUND)
        );
        // blind box
        WalletTransaction transaction = transactionMapper.requestToEnity(request);
        transaction.setStatusTransaction(StatusPayment.SUCCESS);
        Wallet seller = admin.getWallet();
        String message = "Transaction for Blind Box";
        if (order.getBlindBox() == null) {
            message = "Transaction for buy Card";
            transaction.setStatusTransaction(StatusPayment.ESCROWED);
        }
        transaction.setMessage(message);
        transaction.setWalletReceive(seller);
        transaction.setWalletSend(buyer);
        transaction.setAmount(order.getTotalAmount());
        transaction.setOrder(order);
        if (buyer.getBalance() < order.getTotalAmount()) {
            transaction.setStatusTransaction(StatusPayment.FAILED);
            transaction.setMessage(ErrorCode.CAN_NOT_TRANSACTION.getMessage());
        } else {
            admin.getWallet().setBalance(admin.getWallet().getBalance() + order.getTotalAmount());
            usersRepo.save(admin);
            buyer.setBalance(buyer.getBalance() - order.getTotalAmount());
            order.setStatus(OrderStatus.PAID);
            UpdateStatusShipment(order);
            updateQuanity(order);
        }
        return transactionMapper.entityToResponse(transactionRepo.save(transaction));
    }

    public void UpdateStatusShipment(Order order) {
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItem.getShipments().forEach(shipment -> {
                if (shipment.getShipmentStatus() == null) {
                    shipmentService.update(
                            UpdateShipmentRequest.builder()
                                    .shipmentId(shipment.getShipmentId())
                                    .shippingStatus(ShippingStatus.PENDING)
                                    .build(), null
                    );
                }
            });
        }
    }

    public void updateQuanity(Order order) {
        for (OrderItem orderItem : order.getOrderItemList()) {
            orderItem.getListSeller().setQuantity(orderItem.getListSeller().getQuantity() - orderItem.getQuantity());
            listSellerRepo.save(orderItem.getListSeller());
        }
    }

    @Override
    public Page<TransactionResponse> searchByPaymentId(SearchRequest request, UUID paymentId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createAt").descending());
        Specification<WalletTransaction> spe = Specification.allOf(
                TransactionSpecification.byTransactionType(request.getTransactionType()),
                TransactionSpecification.byStatus(request.getStatusPayment()),
                TransactionSpecification.byPaymentId(paymentId)
        );
        return transactionRepo.findAll(spe, pageable).map(transactionMapper::entityToResponse);
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
    public TransactionResponse releasePrice(OrderItem orderItem) {
        Users admin = usersRepo.findByEmail(adminEmail);
        if (admin == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Wallet send = admin.getWallet();
        // Truong hop nhan duoc hang
        Wallet recive = orderItem.getListSeller().getSeller().getWallet();
        Double price = orderItem.getPrice() * orderItem.getQuantity();
        String message = "Release price for orderItem: " + orderItem.getOrderItemId();
        if (orderItem.getOrderItemStatus().equals(OrderItemStatus.CANCELLED)) {
            recive = orderItem.getOrder().getBuyer().getWallet();
            Shipment shipment = orderItem.getShipments().stream().toList().getLast();
            message = "Refund for orderItem: " + orderItem.getOrderItemId();
            if (shipment.getShipmentStatus().equals(ShippingStatus.CANCELLED)) {
                price += shipment.getShipmentFee();
            }
        }
        if(orderItem.getReturnRequest() != null && orderItem.getReturnRequest().getStatus().equals(ReturnRequestStatus.PAID)) {
            recive = orderItem.getOrder().getBuyer().getWallet();
            send = orderItem.getListSeller().getSeller().getWallet();
            message = "Refund for orderItem after recieve card return: " + orderItem.getOrderItemId();
        }
        WalletTransaction transaction = WalletTransaction.builder()
                .amount(orderItem.getPrice() * orderItem.getQuantity())
                .walletReceive(recive)
                .walletSend(send)
                .transactionType(TransactionType.TRANSFER)
                .statusTransaction(StatusPayment.SUCCESS)
                .message(message)
                .build();
        recive.setBalance(recive.getBalance() + (price));
        send.setBalance(send.getBalance() - (price));
        walletRepo.saveAll(List.of(recive, send));
        return transactionMapper.entityToResponse(transactionRepo.save(transaction));
    }

    @Override
    public String payAgaint(UUID paymentId) {
        Payment payment = paymentRepo.findById(paymentId).orElseThrow(
                () -> new AppException(ErrorCode.PAYMENT_NOT_FOUND)
        );
        WalletTransaction transaction = payment.getWalletTransactions().getLast();
        if (transaction != null && transaction.getStatusTransaction().equals(StatusPayment.FAILED)) {
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
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Wallet wallet = getWallet(user.getUserId());
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createAt").descending());
        Specification<WalletTransaction> spe = Specification.allOf(TransactionSpecification.byStatus(statusPayment), TransactionSpecification.byWallet(wallet));
        return transactionRepo.findAll(spe, pageable).map(transactionMapper::entityToResponse);
    }

    @Override
    public TransactionReportResponse report(TransactionReportRequest request) {
        TransactionReportResponse response = TransactionReportResponse.builder().build();
        List<Summaries> summaries = sumariesRepository.findByLocalDateBetween(request.getFrom(), request.getTo());
        if (request.getTo().equals(LocalDate.now())) {
            LocalDateTime start = LocalDate.now().atStartOfDay();
            LocalDateTime end = LocalDateTime.now();
            Summaries s = Summaries.builder()
                    .localDate(LocalDate.now())
                    .totalPayment(transactionRepo.countByCreateAtBetween(start, end))
                    .error(transactionRepo.countByCreateAtBetweenAndStatusTransaction(start, end, StatusPayment.FAILED))
                    .success(transactionRepo.countByCreateAtBetweenAndStatusTransaction(start, end, StatusPayment.SUCCESS))
                    .totalAmount(transactionRepo.sumAmount(start, end))
                    .build();
            summaries.add(s);
        }
        if (summaries != null) {
            summaries.forEach((s) -> {
                response.setTotalError(response.getTotalError() + s.getError());
                response.setTotalSuccess(response.getTotalSuccess() + s.getSuccess());
                response.setTotalPayment(response.getTotalPayment() + s.getTotalPayment());
                response.setTotalAmount(response.getTotalAmount() + s.getTotalAmount());

            });
            response.setTotalPending(response.getTotalPayment() - (response.getTotalPending() + response.getTotalError()));
        }
        response.setData(summaries.stream().map(sumariesMapper::entityToResponse).toList());
        return response;

    }

    @Override
    @Transactional
    public TransactionResponse payforShipFeeReturnItem(UUID returnItemId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = usersRepo.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Wallet sender = getWallet(user.getUserId());
        ReturnRequest returnRequest = returnRequestRepo.findById(returnItemId).orElseThrow(
                () -> new AppException(ErrorCode.RETURN_REQUEST_NOT_FOUND)
        );
        if(!returnRequest.getStatus().equals(ReturnRequestStatus.APPROVED))
        {
            throw new AppException(ErrorCode.CAN_NOT_TRANSACTION_RETURN);
        }

        Shipment shipment = returnRequest.getOrderItemList().getLast().getShipments().stream().toList().getLast();
        shipment.setShipmentStatus(ShippingStatus.PENDING);
        if (shipment.getShipmentStatus() == null) {
            shipmentService.update(
                    UpdateShipmentRequest.builder()
                            .shipmentId(shipment.getShipmentId())
                            .shippingStatus(ShippingStatus.PENDING)
                            .build(), null
            );
        }
        if (sender.getBalance() < shipment.getShipmentFee()) {
            throw new AppException(ErrorCode.CAN_NOT_TRANSACTION);
        }
        if (!returnRequest.getStatus().equals(ReturnRequestStatus.APPROVED)) {
            throw new AppException(ErrorCode.CAN_NOT_TRANSACTION_RETURN);
        }

        Users admin = usersRepo.findByEmail(adminEmail);
        if (admin == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Wallet receive = admin.getWallet();
        Double price = shipment.getShipmentFee()*1.0;
        WalletTransaction newTransaction = WalletTransaction.builder()
                .amount(price)
                .walletSend(sender)
                .walletReceive(receive)
                .transactionType(TransactionType.TRANSFER)
                .statusTransaction(StatusPayment.SUCCESS)
                .message("Pay for return item: " + returnRequest.getReturnRequestId())
                .build();
        sender.setBalance(sender.getBalance() - price);
         receive.setBalance(receive.getBalance() + price);
         walletRepo.saveAll(List.of(receive, sender));
         returnRequest.setStatus(ReturnRequestStatus.PAID);
         returnRequestRepo.save(returnRequest);
        return transactionMapper.entityToResponse(transactionRepo.save(newTransaction));
    }
    public Wallet getWallet(UUID userId) {
        Users users = usersRepo.findByUserId(userId);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (!users.isActive()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_ACTIVE);
        }
        Wallet wallet = users.getWallet();
        if(wallet.getWalletStatus() != WalletStatus.ACTIVE) {
            throw new AppException(ErrorCode.WALLET_NOT_ACTIVE);
        }
        return users.getWallet();
    }
    @Override
    @Transactional
    public TransactionResponse refundCancleReturn(ReturnRequest request) {
        Users admin = usersRepo.findByEmail(adminEmail);
        if (admin == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Wallet send = admin.getWallet();
        Shipment shipment = request.getOrderItemList().getLast().getShipments().stream().toList().getLast();
        Wallet recive = request.getBuyer().getWallet();
        Double price = shipment.getShipmentFee()*1.0;
        String message = "Refund for cancel return request with shipment: " + shipment.getShipmentId();
        WalletTransaction transaction = WalletTransaction.builder()
                .amount(price)
                .walletReceive(recive)
                .walletSend(send)
                .transactionType(TransactionType.TRANSFER)
                .statusTransaction(StatusPayment.SUCCESS)
                .message(message)
                .build();
        recive.setBalance(recive.getBalance() + (price));
        send.setBalance(send.getBalance() - (price));
        walletRepo.saveAll(List.of(recive, send));
        return transactionMapper.entityToResponse(transactionRepo.save(transaction));
    }

    @Override
    public Page<TransactionResponse> listWithDraw(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size,Sort.by("createAt").ascending());
        Page<WalletTransaction> walletTransactions = transactionRepo.findByTransactionType(TransactionType.REQUEST_WITHDRAW,pageable);
        return walletTransactions.map(transactionMapper::entityToResponse);
    }

}
