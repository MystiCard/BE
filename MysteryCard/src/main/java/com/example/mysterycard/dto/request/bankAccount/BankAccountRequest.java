package com.example.mysterycard.dto.request.bankAccount;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class BankAccountRequest {
    @NotNull(message = "Bank Code is required")
    @Size(max = 50,message = "Bank Code must be from 1 to 50 character")
    private String bankCode;
    @NotNull(message = "Account Number is required")
    @Size(max =255,message = "Account Number must be from 1 to 255 character")
    private String accountNumber;
    @NotNull(message = "Account Name  is required")
    @Size(max = 255,message = "Account Name must be from 1 to 255 character")
    private String accountName;
}
