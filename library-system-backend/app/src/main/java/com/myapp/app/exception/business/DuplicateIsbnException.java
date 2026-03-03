package com.myapp.app.exception.business;

public class DuplicateIsbnException extends BusinessException {
    public DuplicateIsbnException(String message) {
        super(message);
    }
}
