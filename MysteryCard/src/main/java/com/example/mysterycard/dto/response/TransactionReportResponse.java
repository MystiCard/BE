package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TransactionReportResponse {
    private long totalAmount;
    private long totalPayment;
    private long totalSuccess;
    private long totalError;
    private long totalPending;
    private List<SummariesResponse> data;

    @Builder
    @Data
    public static class SummariesResponse{
        private LocalDate localDate;
        private long totalAmount;
        private long totalPayment;
        private long success;
        private long error;
    }
}
