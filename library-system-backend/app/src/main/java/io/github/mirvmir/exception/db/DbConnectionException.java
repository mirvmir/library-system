package io.github.mirvmir.exception.db;

import io.github.mirvmir.exception.AppException;

public class DbConnectionException extends AppException {
    public DbConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}