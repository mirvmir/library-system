package io.github.mirvmir.event;

import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.order.OrderItem;
import io.github.mirvmir.useCases.adapter.integration.payment.event.OrderExpiredEvent;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookUnitRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Objects;

@Component
public class OrderExpiredEventListener {

    private final BookUnitRepository bookUnitRepository;
    private final OrderRepository orderRepository;

    public OrderExpiredEventListener(BookUnitRepository bookUnitRepository,
                                     OrderRepository orderRepository) {
        this.bookUnitRepository = bookUnitRepository;
        this.orderRepository = orderRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(OrderExpiredEvent event) {
        Order order = orderRepository.findById(event.orderId());

        if (order == null) {
            return;
        }

        List<Long> reservedBookUnitIds = order.getItems()
                .stream()
                .map(OrderItem::getBookId)
                .filter(Objects::nonNull)
                .toList();

        bookUnitRepository.releaseReservedUnits(reservedBookUnitIds);
    }
}