package com.example.mysterycard.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderCardRequest {
    private String buyerAddress;
    private Long fromDistrictId;
    private Long toDistrictId;
    private Long toWardId;
    private String buyerPhone;
    private String sellerPhone;
    private String sellerAddress;
    private List<OrderItems> orderItemsList;
    @Data
    @Builder
    public static  class  OrderItems{
        private int quantity;
        private UUID listSellerId;
    }


}
