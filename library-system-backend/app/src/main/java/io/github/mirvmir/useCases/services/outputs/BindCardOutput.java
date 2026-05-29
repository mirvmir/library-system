package io.github.mirvmir.useCases.services.outputs;

public record BindCardOutput(
        Long cardId,
        String maskedPan,
        String paymentSystem
) {
}
