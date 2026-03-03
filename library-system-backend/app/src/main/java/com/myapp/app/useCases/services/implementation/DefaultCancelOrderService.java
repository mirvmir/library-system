package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.order.Order;
import com.myapp.app.exception.business.OrderNotFoundException;
import com.myapp.app.useCases.adapter.repository.interfaces.OrderRepository;
import com.myapp.app.useCases.services.interfaces.CancelOrderService;
import com.myapp.app.useCases.services.inputs.CancelOrderInput;
import com.myapp.app.useCases.services.outputs.CancelOrderOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultCancelOrderService implements CancelOrderService {

    private final OrderRepository orderRepo;

    public DefaultCancelOrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    @Transactional
    public CancelOrderOutput execute(CancelOrderInput input) {
        Order order = orderRepo.findById(input.orderId());
        if (null == order) {
            throw new OrderNotFoundException("Order with ID " + input.orderId() + " not found.");
        }
        order.cancel();
        orderRepo.update(order);

        return new CancelOrderOutput();
    }
}
