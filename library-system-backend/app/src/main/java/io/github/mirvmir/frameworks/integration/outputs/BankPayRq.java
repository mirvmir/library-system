package io.github.mirvmir.frameworks.integration.outputs;

import java.math.BigDecimal;
import java.util.Currency;

public record BankPayRq(
        String cardToken,
        BigDecimal price,
        String orderId,
        String description,
        String callbackUrl
) {
}