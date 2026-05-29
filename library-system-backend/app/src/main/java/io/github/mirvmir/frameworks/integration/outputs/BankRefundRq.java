package io.github.mirvmir.frameworks.integration.outputs;

import java.math.BigDecimal;
import java.util.Currency;

public record BankRefundRq(
        String externalPaymentId,
        String refundId,
        BigDecimal amount,
        Currency currency,
        String reason,
        String webhookUrl
) {
}