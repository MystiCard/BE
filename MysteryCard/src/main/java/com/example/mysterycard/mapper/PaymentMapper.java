package com.example.mysterycard.mapper;

import com.example.mysterycard.dto.request.transaction.TransactionRequest;
import com.example.mysterycard.dto.response.PaymentResponse;
import com.example.mysterycard.dto.response.transaction.TransactionResponse;
import com.example.mysterycard.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    PaymentResponse entityToResponse(Payment payment);
}
