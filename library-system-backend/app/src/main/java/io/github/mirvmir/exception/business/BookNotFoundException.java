package io.github.mirvmir.exception.business;

public class BookNotFoundException extends BusinessException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
