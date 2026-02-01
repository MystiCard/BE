package com.example.mysterycard.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // AUTHENCATION ERRORS(1)
    INVALID_AUTHENCATION(1001, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    INVALID_LOGIN_REQUEST(1002, "username or password wrong", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_VERYFY(1003,"Email not verify. Please input code in your email" ,HttpStatus.NOT_FOUND),
   INVALID_CODE(1004,"Code verify not right. Please input again",HttpStatus.NOT_FOUND),
    EMAIL_VERIFED(1005, "Email verified",HttpStatus.BAD_REQUEST),
    // TOKEN ERRORS(2)
    REFRESH_TOKEN_NOT_FOUND(2001, "Refresh token not found", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_EXPIRED(2002, "Refresh token has expired.Please make a new login request", HttpStatus.BAD_REQUEST),

    // USERS ERRORS(3)
    USER_NOT_FOUND(3001, "User not found ", HttpStatus.NOT_FOUND),
    USER_INACTIVE(3002, "User is inactive", HttpStatus.FORBIDDEN),
    USER_EXISTED(3003, "Email existed.Please input another email", HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(3004, "Phone existed. Please input another phone", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_ACTIVE(3005,"Account invalid can do this function",HttpStatus.BAD_REQUEST),
    // Permision error (4)
    PERMISION_CODE_EXISTED(4001, "Permision Code existed. Please input Permision Code again", HttpStatus.BAD_REQUEST),
    PERMISION_CODE_NOT_FOUND(4002, "Permision not found with this code", HttpStatus.NOT_FOUND),

    //CATEGORY ERRORS(4)
    CATEGORY_NOT_FOUND(4001, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_DUPLICATE(4002, "Category already exists", HttpStatus.CONFLICT),
    EMPTY_CATEGORY_OR_CARDS(4003, "Cannot delete category because it contains sub-categories or cards", HttpStatus.BAD_REQUEST),

    //CARD ERRORS(6)
    CARD_NOT_FOUND(6001, "Card not found", HttpStatus.NOT_FOUND),
    CARD_DUPLICATE(6002, "Card already exists", HttpStatus.CONFLICT),

    // Role Erreer(5)
    ROLE_NOT_FOUND(5001, "Role not found with this code", HttpStatus.NOT_FOUND),
    ROLE_EXISTED(5002, "Role existed. Please enter another Role code", HttpStatus.BAD_REQUEST),

    // Bank Account (7)
    BANK_ACCOUNT_NOT_FOUND(7001,"Bank Account Not Found ",HttpStatus.NOT_FOUND),
    BANK_CODE_EXISTED(7002,"Bank Code existed. Please input again bank code ",HttpStatus.BAD_REQUEST),
    BANK_DUPLICATIN(7003,"You already have this bank account",HttpStatus.BAD_REQUEST),

    // Blind Box (8)
    BLIND_BOX_NOT_FOUND(8001,"Blind Box Not Found",HttpStatus.NOT_FOUND),
    BLIND_BOX_ALREADY_EXISTS(8002,"Blind Box Already Exists",HttpStatus.BAD_REQUEST),
    DRAW_CARD_FAILED(8003,"Draw Card Failed",HttpStatus.BAD_REQUEST),
    EMPTY_BOX(8004,"Blind Box is empty. Please contact admin to refill",HttpStatus.BAD_REQUEST),
    //Rate Config (9)
    RATE_CONFIG_NOT_FOUND(9001,"Rate Config Not Found",HttpStatus.NOT_FOUND),
    RATE_CONFIG_ALREADY_EXISTS(9002,"Rate Config Already Exists",HttpStatus.BAD_REQUEST),

    //Blind Box Purchase (10)
    BLIND_BOX_PURCHASE_NOT_FOUND(10001,"Blind Box Purchase Not Found",HttpStatus.NOT_FOUND),

    // Transaction (11)
    TRANSACTION_NOT_FOUND(11001, "Transaction not found", HttpStatus.BAD_REQUEST),
    PAYMENT_HASH_DATA_FAIL(11002,"Hash data to payment MOMO fail",HttpStatus.BAD_REQUEST),
    CAN_NOT_WITHDRAW(11003,"Amount withdraw larger than balance in wallet",HttpStatus.BAD_REQUEST),
    CAN_NOT_TRANSACTION(11004,"Balance not enough to process transaction. PLease deposite money into wallet",HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_FOUND(11005,"Payment not found with transaction ref",HttpStatus.NOT_FOUND),
    // Order (12)
    ORDER_NOT_FOUND(12001, "Order not found with code", HttpStatus.NOT_FOUND),
    //IMPORT EXPORT ERRORS(13)
    FILE_IMPORT_ERROR(13001,"File import error. Please check your file",HttpStatus.BAD_REQUEST),

;
    int code;
    String message;
    HttpStatus status;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
