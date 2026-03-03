package com.myapp.app.exception.db;

import com.myapp.app.exception.AppException;

public class DbTransactionException extends AppException {
    public DbTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}