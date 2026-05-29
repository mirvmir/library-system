package io.github.mirvmir.frameworks.integration.outputs;

import java.math.BigDecimal;

public record BankBindCardRq(
        String cardNumber,
        String cardHolder,
        String expiryMonth,
        String expiryYear,
        String cvc,
        BigDecimal verificationAmount,
        String idempotencyKey
) {
}
