package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.transaction.UpdateTransactionStatusRequest;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.exception.ErrorCode;
import com.example.mysterycard.service.PaymentService;
import com.example.mysterycard.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {
    private final TransactionService transactionService;
    private final PaymentService paymentService;
    @GetMapping("/call-back")
    public ResponseEntity<ApiResponse<?>> handleIPN(HttpServletRequest request) {



      UUID orderId = UUID.fromString(request.getParameter("orderId"));
    int resultCode = Integer.parseInt(request.getParameter("resultCode"));
    String message = request.getParameter("message");
         UpdateTransactionStatusRequest updateTransactionStatusRequest = UpdateTransactionStatusRequest.builder()
                 .tranferId(orderId)
                 .message(message)
                 .build();
        log.info("Veryfy signature {}",paymentService.veryfySignature(request));
         if(paymentService.veryfySignature(request))
         {
             if (resultCode == 0) {
                 updateTransactionStatusRequest.setStatusPayment(StatusPayment.SUCCESS);
                 System.out.println("Thanh toán thành công: " + orderId);
             } else {

                 updateTransactionStatusRequest.setStatusPayment(StatusPayment.FAILED);
                 System.out.println("Thanh toán thất bại: " + orderId);
             }
         }
        else{
            updateTransactionStatusRequest.setMessage("Veryfy signature wrong");
            updateTransactionStatusRequest.setStatusPayment(StatusPayment.FAILED);
         }
        log.info("Call back {},{}",orderId,resultCode);
        return ResponseEntity.ok(ApiResponse.success(transactionService.updateTransactionStatus(updateTransactionStatusRequest)) );
    }
}
