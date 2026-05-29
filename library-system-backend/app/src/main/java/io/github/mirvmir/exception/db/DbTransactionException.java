package io.github.mirvmir.exception.db;

import io.github.mirvmir.exception.AppException;

public class DbTransactionException extends AppException {
    public DbTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}