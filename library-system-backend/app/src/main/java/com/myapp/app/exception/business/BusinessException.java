package com.myapp.app.exception.business;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
