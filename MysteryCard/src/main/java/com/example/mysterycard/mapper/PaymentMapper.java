package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.transaction.TransactionRequest;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    TransactionResponse.PaymentResponse entityToResponse(Payment payment);
}
