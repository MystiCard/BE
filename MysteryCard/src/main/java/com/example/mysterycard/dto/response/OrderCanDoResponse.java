package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCanDoResponse {
    private boolean canCancle;
    private boolean canConfirmRecieve;
}
