package io.github.mirvmir.useCases.services.mapping;

import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.order.OrderItem;
import io.github.mirvmir.useCases.services.outputs.GetOrderOutput;

import java.util.List;

public class OrderToGetOrderRsMapper {
    public static GetOrderOutput map(Order order) {

        List<String> isbns = order.getItems().stream()
                .map(OrderItem::getBookIsbn)
                .toList();

        return new GetOrderOutput(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getCompletionAt(),
                order.getTotalPrice(),
                isbns
        );
    }
}
