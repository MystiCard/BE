package com.example.mysterycard.service;

import com.example.mysterycard.entity.Payment;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface PaymentService {
    String createPayment(Payment payment);
    boolean veryfySignature(HttpServletRequest request) ;
}
