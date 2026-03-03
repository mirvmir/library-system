package com.myapp.app.useCases.services.mapping;

import com.myapp.app.domain.entities.order.Order;
import com.myapp.app.domain.entities.order.OrderItem;
import com.myapp.app.useCases.services.outputs.GetOrderOutput;

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
                order.getCompletionDate(),
                order.getTotalPrice(),
                isbns
        );
    }
}
