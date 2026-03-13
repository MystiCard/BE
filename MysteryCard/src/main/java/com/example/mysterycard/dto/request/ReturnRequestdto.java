package com.example.mysterycard.dto.request;

import com.example.mysterycard.enums.ReturnRequestStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReturnRequestdto {
    private List<UUID> orderItemIds;
    @NotNull(message = "Reason is required")
    private String reason;
    private String sendAddress;
    @NotNull(message = "Send District Id is required")
    private Long sendDistrictId;
    @NotNull(message = "Send Ward Id is required")
    private Long sendWardId;
    @NotNull(message = "SendPhone is required")
    @Pattern(regexp = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$",message = "Phone must be start 0 or +84 followed by 9 digit  ")
    private String sendPhone;
}
