package com.example.mysterycard.exception;

import lombok.Data;

@Data
public class AppException extends RuntimeException {
     ErrorCode errorCode;
    public AppException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }


}
