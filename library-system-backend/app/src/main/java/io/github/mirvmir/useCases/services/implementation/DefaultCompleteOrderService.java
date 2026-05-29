package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.exception.business.BusinessException;
import io.github.mirvmir.exception.business.OrderNotFoundException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.services.interfaces.CompleteOrderService;
import io.github.mirvmir.useCases.services.inputs.CompleteOrderInput;
import io.github.mirvmir.useCases.services.outputs.CompleteOrderOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class DefaultCompleteOrderService implements CompleteOrderService {

    private final OrderRepository orderRepo;

    public DefaultCompleteOrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    @Transactional
    public CompleteOrderOutput execute(CompleteOrderInput input) {
        Order order = orderRepo.findByIdForUpdate(input.orderId());

        if (order == null) {
            throw new OrderNotFoundException(
                    "Order with ID " + input.orderId() + " not found."
            );
        }

        if (!order.isPayed()) {
            throw new BusinessException("Нельзя завершить неоплаченный заказ.");
        }

        order.complete(LocalDateTime.now());
        orderRepo.update(order);

        return new CompleteOrderOutput();
    }
}