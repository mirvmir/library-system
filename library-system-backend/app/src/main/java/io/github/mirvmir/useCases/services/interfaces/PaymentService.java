package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.controllers.web.requests.BankPaymentWebhookRq;
import io.github.mirvmir.controllers.web.requests.BankPayoutWebhookRq;
import io.github.mirvmir.controllers.web.requests.BankRefundWebhookRq;
import io.github.mirvmir.controllers.web.requests.BindCardRq;
import io.github.mirvmir.useCases.services.outputs.BindCardOutput;
import io.github.mirvmir.useCases.services.outputs.ConfirmPaymentOutput;

import java.util.List;

public interface PaymentService {
    BindCardOutput bindCard(BindCardRq request);
    List<BindCardOutput> getMyCard();
    void setDefaultCard(Long cardId);
    ConfirmPaymentOutput startPayment(Long paymentId, Long cardId);
    void handleBankWebhook(BankPaymentWebhookRq request);
    void handleBankPayoutWebhook(BankPayoutWebhookRq request);
    void handleBankRefundWebhook(BankRefundWebhookRq request);
}