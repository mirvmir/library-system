package io.github.mirvmir.event;

import io.github.mirvmir.useCases.adapter.integration.payment.event.OrderExpiredEvent;
import io.github.mirvmir.useCases.adapter.integration.payment.event.OrderPayedEvent;
import io.github.mirvmir.useCases.adapter.integration.payment.event.OrderRefundedEvent;
import io.github.mirvmir.useCases.adapter.integration.payment.event.PayoutSucceededEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public EventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishOrderPayed(OrderPayedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void publishOrderRefunded(OrderRefundedEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void publishPayoutSucceeded(PayoutSucceededEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void publishOrderExpired(OrderExpiredEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}