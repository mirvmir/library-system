package com.myapp.app.exception.business;

public class OrderImportException extends RuntimeException {
    public OrderImportException(String message) {
        super(message);
    }
}
