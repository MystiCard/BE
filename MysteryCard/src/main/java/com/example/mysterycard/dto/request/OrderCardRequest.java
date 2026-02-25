package com.example.mysterycard.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderCardRequest {
    @Size(min = 5, message = "Address mus be large than 5 character")
    private String buyerAddress;
//    @NotNull(message = "From District Id is required")
//    private Long fromDistrictId;
    @NotNull(message = "To District Id is required")
    private Long toDistrictId;
    @NotNull(message = "To Ward Id  is required")
    private Long toWardId;
    @NotNull(message = "Phone is required")
    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$",message = "Phone must be start 0 or +84 followed by 9 digit  ")
    private String buyerPhone;
    @NotNull(message = "Order items not null")
    private List<OrderItems> orderItemsList;
    @Data
    @Builder
    public static  class  OrderItems{
        @Min(value = 1, message = "Quantity must be larger than 1")
        private int quantity;
        @NotNull(message = "List seller Id is require")
        private UUID listSellerId;
    }


}
