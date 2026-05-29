package io.github.mirvmir.frameworks.integration.outputs;

import java.math.BigDecimal;
import java.util.Currency;

public record BankPayoutRq(
        String cardToken,
        BigDecimal amount,
        Currency currency,
        String payoutId,
        String description,
        String webhookUrl
) {
}