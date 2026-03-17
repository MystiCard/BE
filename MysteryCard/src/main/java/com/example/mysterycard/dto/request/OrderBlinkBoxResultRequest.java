package com.example.mysterycard.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderBlinkBoxResultRequest {
    @Size(min = 5, message = "Address must be larger than 5 characters")
    private String buyerAddress;

    @NotNull(message = "To District Id is required")
    private Long toDistrictId;

    @NotNull(message = "To Ward Id is required")
    private Long toWardId;

    @NotNull(message = "Phone is required")
    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$",
            message = "Phone must start with 0 or +84 followed by 9 digits")
    private String buyerPhone;

    @NotNull(message = "BlindBox results cannot be null")
    private List<UUID> blindBoxResultIds;
    @NotNull(message = "Name is required")
    private String toName;
}
