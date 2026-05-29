package io.github.mirvmir.controllers.web.controller;

import io.github.mirvmir.controllers.web.requests.*;
import io.github.mirvmir.useCases.services.interfaces.PaymentService;
import io.github.mirvmir.useCases.services.outputs.BindCardOutput;
import io.github.mirvmir.useCases.services.outputs.ConfirmPaymentOutput;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/card/bind")
    public List<BindCardOutput> getCard() {
        return paymentService.getMyCard();
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/card/bind")
    @ResponseStatus(HttpStatus.CREATED)
    public BindCardOutput bindCard(
            @RequestBody
            BindCardRq request
    ) {
        return paymentService.bindCard(request);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PatchMapping("/card/bind")
    public void setDefaultCard(
            @RequestBody
            DefaultCardRq request
    ) {
        paymentService.setDefaultCard(request.cardId());
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping("/{paymentId}/start")
    public ConfirmPaymentOutput startPayment(
            @PathVariable("paymentId")
            Long paymentId,
            @RequestBody(required = false)
            ConfirmPaymentRq request
    ) {
        Long cardId = request == null
                ? null
                : request.cardId();
        return paymentService.startPayment(paymentId, cardId);
    }

    @PostMapping("/webhook/bank")
    @ResponseStatus(HttpStatus.OK)
    public void handleBankWebhook(
            @RequestBody
            BankPaymentWebhookRq request
    ) {
        paymentService.handleBankWebhook(request);
    }

    @PostMapping("/webhook/bank/payout")
    @ResponseStatus(HttpStatus.OK)
    public void handleBankPayoutWebhook(
            @RequestBody
            BankPayoutWebhookRq request
    ) {
        paymentService.handleBankPayoutWebhook(request);
    }

    @PostMapping("/webhook/bank/refund")
    @ResponseStatus(HttpStatus.OK)
    public void handleBankRefundWebhook(
            @RequestBody
            BankRefundWebhookRq request
    ) {
        paymentService.handleBankRefundWebhook(request);
    }
}