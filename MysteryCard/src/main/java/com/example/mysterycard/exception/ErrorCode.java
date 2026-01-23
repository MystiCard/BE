package com.example.mysterycard.exception;

import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ErrorCode {
    // AUTHENCATION ERRORS(1)
    INVALID_AUTHENCATION(1001, "Invalid credentials", HttpStatus.UNAUTHORIZED),
    INVALID_LOGIN_REQUEST(1002, "username or password wrong", HttpStatus.BAD_REQUEST),

    // TOKEN ERRORS(2)
    REFRESH_TOKEN_NOT_FOUND(2001, "Refresh token not found", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_EXPIRED(2002, "Refresh token has expired.Please make a new login request", HttpStatus.BAD_REQUEST),

    // USERS ERRORS(3)
    USER_NOT_FOUND(3001, "User not found ", HttpStatus.NOT_FOUND),
    USER_INACTIVE(3002, "User is inactive", HttpStatus.FORBIDDEN),
    USER_EXISTED(3003,"Email existed.Please input another email",HttpStatus.BAD_REQUEST),
    PHONE_EXISTED(3004,"Phone existed. Please input another phone",HttpStatus.BAD_REQUEST),
    // Permision error (4)
    PERMISION_CODE_EXISTED(4001,"Permision Code existed. Please input Permision Code again", HttpStatus.BAD_REQUEST),
    PERMISION_CODE_NOT_FOUND(4002,"Permision not found with this code",HttpStatus.NOT_FOUND),

    //CATEGORY ERRORS(4)
    CATEGORY_NOT_FOUND(4001, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_DUPLICATE(4002, "Category already exists", HttpStatus.CONFLICT),

    //CARD ERRORS(6)
    CARD_NOT_FOUND(6001, "Card not found", HttpStatus.NOT_FOUND),
    CARD_DUPLICATE(6002, "Card already exists", HttpStatus.CONFLICT),

    // Role Erreer(5)
    ROLE_NOT_FOUND(5001, "Role not found with this code",HttpStatus.NOT_FOUND),
    ROLE_EXISTED(5002, "Role existed. Please enter another Role code", HttpStatus.BAD_REQUEST),

      // Bank Account (7)
       BANK_ACCOUNT_NOT_FOUND(7001,"Bank Account Not Found ",HttpStatus.NOT_FOUND),
       BANK_CODE_EXISTED(7002,"Bank Code existed. Please input again bank code ",HttpStatus.BAD_REQUEST),
       BANK_DUPLICATIN(7003,"You already have this bank account",HttpStatus.BAD_REQUEST),
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
