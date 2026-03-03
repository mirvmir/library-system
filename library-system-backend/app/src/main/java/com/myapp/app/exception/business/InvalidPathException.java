package com.myapp.app.exception.business;

public class InvalidPathException extends RuntimeException {
    public InvalidPathException(String message) {
        super(message);
    }
}
