package io.github.mirvmir.exception.integration;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}
