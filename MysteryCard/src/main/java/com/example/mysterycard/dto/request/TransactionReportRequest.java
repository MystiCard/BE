package com.example.mysterycard.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionReportRequest {
    private LocalDate from;
    private  LocalDate to;
}
