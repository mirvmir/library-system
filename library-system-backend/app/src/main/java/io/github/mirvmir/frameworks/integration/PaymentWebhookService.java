package io.github.mirvmir.frameworks.integration;

import java.time.LocalDateTime;

public interface PaymentWebhookService {
    void handlePaymentSucceeded(Long paymentId,
                                String externalPaymentId,
                                LocalDateTime paidAt);
}
