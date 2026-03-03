package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.customer.Customer;
import com.myapp.app.domain.entities.order.Order;
import com.myapp.app.domain.entities.order.OrderItem;
import com.myapp.app.exception.business.OrderNotFoundException;
import com.myapp.app.useCases.adapter.repository.interfaces.CustomerRepository;
import com.myapp.app.useCases.adapter.repository.interfaces.OrderRepository;
import com.myapp.app.useCases.services.inputs.GetOrderDescriptionInput;
import com.myapp.app.useCases.services.interfaces.GetOrderDescriptionService;
import com.myapp.app.useCases.services.outputs.OrderDescriptionOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultGetOrderDescriptionService implements GetOrderDescriptionService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;

    public DefaultGetOrderDescriptionService(OrderRepository orderRepo,
                                             CustomerRepository customerRepo) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    @Transactional
    public OrderDescriptionOutput execute(GetOrderDescriptionInput input) {
        Order order = orderRepo.findById(input.orderId());

        if (null == order) {
            throw new OrderNotFoundException("Order with ID " + input.orderId()
                    + " not found.");
        }

        Customer customer = customerRepo.findById(order.getCustomerId());

        return new OrderDescriptionOutput(order.getId(),
                customer.getId(),
                order.getStatus().toString(),
                order.getCompletionDate(),
                order.getCreatedDate(),
                order.getTotalPrice(),
                order.getItems().stream().map(OrderItem::getBookIsbn).toList());
    }
}
