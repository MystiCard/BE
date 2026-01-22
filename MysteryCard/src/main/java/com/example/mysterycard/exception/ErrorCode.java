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

    // Permision error (4)
    PERMISION_CODE_EXISTED(4001,"Permision Code existed. Please input Permision Code again", HttpStatus.BAD_REQUEST),
    PERMISION_CODE_NOT_FOUND(4002,"Permision not found with this code",HttpStatus.NOT_FOUND),
    // Role Erreer(5)
    ROLE_NOT_FOUND(5001, "Role not found with this code",HttpStatus.NOT_FOUND),
    ROLE_EXISTED(5002, "Role existed. Please enter another Role code", HttpStatus.BAD_REQUEST);
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
