package com.example.mysterycard.service.impl;

import com.example.mysterycard.configuration.MomoConfig;
import com.example.mysterycard.dto.request.MomoRequest;
import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.dto.response.PaymentResponse;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.Payment;
import com.example.mysterycard.entity.Users;
import com.example.mysterycard.entity.WalletTransaction;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.exception.AppException;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.mapper.PaymentMapper;
import com.example.mysterycard.repository.PaymentRepo;
import com.example.mysterycard.repository.TransactionRepo;
import com.example.mysterycard.repository.UsersRepo;
import com.example.mysterycard.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final MomoConfig config;
    private RestTemplate restTemplate = new RestTemplate();
    private final PaymentRepo paymentRepo;
    private final PaymentMapper paymentMapper;
    private final UsersRepo usersRepo;
    private final TransactionRepo transactionRepo;


    @Override
    public String createPayment(Payment payment) {
        String orderId = payment.getTransactionRef();
        String requestId = UUID.randomUUID().toString();
        String extraData = "";

        String rawSignature = String.format(
                "accessKey=%s&amount=%d&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                config.getAccessKey(), payment.getAmount(), extraData, config.getIpnUrl(),
                orderId, payment.getContent(), config.getPartnerCode(),
                config.getRedirectUrl(), requestId, config.getRequestType()
        );
        log.info("Raw {}", rawSignature);
        String signature;
        try {
            signature = hmacSHA256(rawSignature, config.getSecretKey());

        } catch (Exception e) {
            throw new AppException(ErrorCode.PAYMENT_HASH_DATA_FAIL);
        }
        log.info("Signature {}", signature);
        log.info("Redirect URL {}", config.getRedirectUrl());
        MomoRequest request = new MomoRequest();
        request.setPartnerCode(config.getPartnerCode());
        request.setAccessKey(config.getAccessKey());
        request.setRequestId(requestId);
        request.setOrderId(orderId);
        request.setAmount(payment.getAmount());
        request.setOrderInfo(payment.getContent());
        request.setRedirectUrl(config.getRedirectUrl());
        request.setIpnUrl(config.getIpnUrl());
        request.setRequestType(config.getRequestType());
        request.setExtraData(extraData);
        request.setSignature(signature);
        org.springframework.http.HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MomoRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                config.getEndPoint() + "/create",
                entity,
                Map.class
        );

        return response.getBody().get("payUrl").toString();
    }

    @Override
    public boolean veryfySignature(HttpServletRequest request) {
        Payment payment = paymentRepo.findByTransactionRef(request.getParameter("orderId"));
        if (payment == null) {
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        payment.setPayType(request.getParameter("payType"));
        paymentRepo.save(payment);
        String receivedSignature = request.getParameter("signature");
        if (receivedSignature == null) return false;

        try {
            String rawData = "accessKey=" + config.getAccessKey() +
                    "&amount=" + request.getParameter("amount") +
                    "&extraData=" + request.getParameter("extraData") +
                    "&message=" + request.getParameter("message") +
                    "&orderId=" + request.getParameter("orderId") +
                    "&orderInfo=" + request.getParameter("orderInfo") +
                    "&orderType=" + request.getParameter("orderType") +
                    "&partnerCode=" + request.getParameter("partnerCode") +
                    "&payType=" + request.getParameter("payType") +
                    "&requestId=" + request.getParameter("requestId") +
                    "&responseTime=" + request.getParameter("responseTime") +
                    "&resultCode=" + request.getParameter("resultCode") +
                    "&transId=" + request.getParameter("transId");

            String hash = hmacSHA256(rawData, config.getSecretKey());

            log.info("RAW = {}", rawData);
            log.info("HASH = {}", hash);
            log.info("RECEIVED = {}", receivedSignature);

            return receivedSignature.equalsIgnoreCase(hash);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Page<PaymentResponse> getAll(StatusPayment statusPayment, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt"));
        if (statusPayment == null) {
            return paymentRepo.findAll(pageable).map(paymentMapper::entityToResponse);
        }
        return paymentRepo.findByStatusPayment(statusPayment, pageable).map(paymentMapper::entityToResponse);
    }

    @Override
    public PaymentResponse getPayment(UUID id) {
        Payment payment = paymentRepo.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PAYMENT_NOT_FOUND)
        );
        return paymentMapper.entityToResponse(payment);
    }

    @Override
    public PageResponse<PaymentResponse> getMyPayment(StatusPayment statusPayment, int page, int size) {
        page--;
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Users users = usersRepo.findByEmail(email);
        if (users == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Set<WalletTransaction> walletTransactions = users.getWallet().getReceivedTransactions();
        Set<WalletTransaction> walletTransactions1 = users.getWallet().getSentTransactions();
        if (walletTransactions == null) {
            walletTransactions = new HashSet<>();
        }
        if (walletTransactions1 != null) {
            walletTransactions.addAll(walletTransactions1);
        }

        Set<Payment> payments = new HashSet<>();
        walletTransactions.forEach(transaction -> {
            if (transaction.getPayment() != null) {
                if (statusPayment != null && transaction.getStatusTransaction().equals(statusPayment)) {
                    payments.add(transaction.getPayment());
                } else {
                    payments.add(transaction.getPayment());
                }

            }

        });
        int totalPages = payments.size() / size;
        int from = page * size;
        int to = Math.min(((page + 1) * size), payments.size());
        List<Payment> paymentList = payments.stream().toList().subList(from, to);
        PageResponse<PaymentResponse> pageResponse = PageResponse.<PaymentResponse>builder()
                .content(paymentList.stream().map(paymentMapper::entityToResponse).collect(Collectors.toList()))
                .totalElements(paymentList.size())
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .last(to == paymentList.size())
                .build();

        return pageResponse;
    }

    @Override
    public PaymentResponse getByTransactionId(UUID transactionId) {
        WalletTransaction transaction = transactionRepo.findById(transactionId).orElseThrow(
                ()-> new AppException(ErrorCode.TRANSACTION_NOT_FOUND)
        );
        if(transaction.getPayment() == null) {
            throw new AppException(ErrorCode.PAYMENT_NOT_FOUND);
        }
        return paymentMapper.entityToResponse(transaction.getPayment());
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder(2 * rawHmac.length);
        for (byte b : rawHmac) {
            String s = Integer.toHexString(0xff & b);
            if (s.length() == 1) hex.append('0');
            hex.append(s);
        }
        return hex.toString();
    }

}
