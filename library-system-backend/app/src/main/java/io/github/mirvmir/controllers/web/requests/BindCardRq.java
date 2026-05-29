package io.github.mirvmir.controllers.web.requests;

public record BindCardRq(
        String cardNumber,
        String cardHolder,
        String expiryMonth,
        String expiryYear,
        String cvc
) {
}
