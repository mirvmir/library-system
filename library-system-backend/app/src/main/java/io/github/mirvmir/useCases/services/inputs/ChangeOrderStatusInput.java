package io.github.mirvmir.useCases.services.inputs;

import io.github.mirvmir.domain.entities.order.OrderStatus;

public record ChangeOrderStatusInput(Long orderId, OrderStatus orderStatus) {
}
