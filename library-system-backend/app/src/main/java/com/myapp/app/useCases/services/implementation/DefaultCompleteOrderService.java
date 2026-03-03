package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.order.Order;
import com.myapp.app.domain.entities.order.OrderItem;
import com.myapp.app.exception.business.OrderNotFoundException;
import com.myapp.app.useCases.adapter.repository.interfaces.BookUnitRepository;
import com.myapp.app.useCases.adapter.repository.interfaces.OrderRepository;
import com.myapp.app.useCases.services.interfaces.CompleteOrderService;
import com.myapp.app.useCases.services.inputs.CompleteOrderInput;
import com.myapp.app.useCases.services.outputs.CompleteOrderOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DefaultCompleteOrderService implements CompleteOrderService {

    private final OrderRepository orderRepo;
    private final BookUnitRepository unitRepo;

    public DefaultCompleteOrderService(OrderRepository orderRepo,
                                       BookUnitRepository unitRepo) {
        this.orderRepo = orderRepo;
        this.unitRepo = unitRepo;
    }

    @Override
    @Transactional
    public CompleteOrderOutput execute(CompleteOrderInput input) {
        Order order = orderRepo.findById(input.orderId());
        if (null == order) {
            throw new OrderNotFoundException("Order with ID " + input.orderId() + " not found.");
        }

        List<String> isbns = order.getItems().stream()
                .map(OrderItem::getBookIsbn)
                .toList();

        List<Long> reservedBookUnitId = unitRepo.reserveBookUnitId(isbns);

        order.reserve(reservedBookUnitId);
        order.complete(LocalDateTime.now());
        orderRepo.update(order);

        return new CompleteOrderOutput();
    }
}
