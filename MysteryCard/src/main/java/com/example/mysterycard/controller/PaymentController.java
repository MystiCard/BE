package com.example.mysterycard.controller;

import com.example.mysterycard.base.ApiResponse;
import com.example.mysterycard.dto.request.transaction.SearchRequest;
import com.example.mysterycard.dto.request.transaction.UpdateTransactionStatusRequest;
import com.example.mysterycard.dto.response.PageResponse;
import com.example.mysterycard.dto.response.PaymentResponse;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.enums.StatusPayment;
import com.example.mysterycard.service.PaymentService;
import com.example.mysterycard.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(ApiResponse.success(transactionService.callBackDepositeAndWithdraw(updateTransactionStatusRequest)) );
    }
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<PageResponse<PaymentResponse>>> me(
            @RequestParam(required = false) StatusPayment statusPayment,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getMyPayment(statusPayment,page,size)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getAllPayments(
            @RequestParam(required = false) StatusPayment statusPayment,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAll(statusPayment,page,size)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(@PathVariable UUID id){
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPayment(id)));
    }
    @GetMapping("/transactions/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByTransactinId(
            @PathVariable UUID transactionId
    ) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getByTransactionId(transactionId)));
    }
}
