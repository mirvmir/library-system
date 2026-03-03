package com.myapp.app.exception.business;

public class BookNotFoundException extends BusinessException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
