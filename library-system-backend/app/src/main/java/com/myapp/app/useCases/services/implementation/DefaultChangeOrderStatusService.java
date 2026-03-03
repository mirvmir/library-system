package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.order.Order;
import com.myapp.app.exception.business.OrderNotFoundException;
import com.myapp.app.useCases.adapter.repository.interfaces.OrderRepository;
import com.myapp.app.useCases.services.interfaces.ChangeOrderStatusService;
import com.myapp.app.useCases.services.inputs.ChangeOrderStatusInput;
import com.myapp.app.useCases.services.outputs.ChangeOrderStatusOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultChangeOrderStatusService implements ChangeOrderStatusService {

    private final OrderRepository orderRepo;

    public DefaultChangeOrderStatusService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    @Transactional
    public ChangeOrderStatusOutput execute(ChangeOrderStatusInput input) {
        Order order = orderRepo.findById(input.orderId());
        if (null == order) {
            throw new OrderNotFoundException("Order with ID " + input.orderId() + " not found.");
        }
        order.changeStatus(input.orderStatus());
        orderRepo.update(order);

        return new ChangeOrderStatusOutput();
    }
}
