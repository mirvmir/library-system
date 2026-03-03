package com.myapp.app.useCases.services.inputs;

import com.myapp.app.domain.entities.order.OrderStatus;

public record ChangeOrderStatusInput(Long orderId, OrderStatus orderStatus) {
}
