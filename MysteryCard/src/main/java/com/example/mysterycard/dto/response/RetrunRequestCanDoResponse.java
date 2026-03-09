package com.example.mysterycard.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RetrunRequestCanDoResponse {
    private boolean canCancle;
    private boolean canjectOrApproved;
    private boolean canPayment;

}
