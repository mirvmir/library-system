package com.myapp.app.exception.db;

import com.myapp.app.exception.AppException;

public class DbConnectionException extends AppException {
    public DbConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}