package io.github.mirvmir.exception.business;

public class PaymentNotFoundException extends BusinessException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
