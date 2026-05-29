package io.github.mirvmir.useCases.adapter.integration.payment.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PayoutSucceededEvent(
        Long payoutId,
        Long walletWithdrawalId,
        Long userId,
        BigDecimal amount,
        LocalDateTime paidAt
) {
}