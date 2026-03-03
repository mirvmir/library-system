package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.order.Order;
import com.myapp.app.exception.business.IncompatibleSortTypesException;
import com.myapp.app.useCases.adapter.repository.interfaces.OrderRepository;
import com.myapp.app.useCases.services.interfaces.GetOrderService;
import com.myapp.app.useCases.services.mapping.OrderToGetOrderRsMapper;
import com.myapp.app.useCases.services.inputs.GetOrderInput;
import com.myapp.app.useCases.services.outputs.GetOrdersOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class DefaultGetOrderService implements GetOrderService {

    private final OrderRepository orderRepo;

    public DefaultGetOrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    @Transactional
    public GetOrdersOutput execute(GetOrderInput input) {
        if ("ORDER".equals(input.type()) && "COMPLETION_DATE".equals(input.field())) {
            List<Order> orders = orderRepo.findAll().stream()
                    .sorted(Comparator.comparing(Order::getCompletionDate,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
            return new GetOrdersOutput(orders.stream().map(OrderToGetOrderRsMapper::map).toList());
        }
        if ("ORDER".equals(input.type()) && "PRICE".equals(input.field())) {
            List<Order> orders;
            if ("ASC".equals(input.direction())) {
                orders = orderRepo.findAll()
                        .stream()
                        .sorted(Comparator.comparing(Order::getTotalPrice))
                        .toList();
            } else {
                orders = orderRepo.findAll()
                        .stream()
                        .sorted(Comparator.comparing(Order::getTotalPrice,
                                Comparator.reverseOrder()))
                        .toList();
            }
            return new GetOrdersOutput(orders.stream().map(OrderToGetOrderRsMapper::map).toList());
        }
        if ("ORDER".equals(input.type()) && "STATUS".equals(input.field())) {
            List<Order> orders;
            if ("ASC".equals(input.direction())) {
                orders = orderRepo.findAll()
                        .stream()
                        .sorted(Comparator.comparing(Order::getStatus))
                        .toList();
            } else {
                orders = orderRepo.findAll()
                        .stream()
                        .sorted(Comparator.comparing(Order::getStatus,
                                Comparator.reverseOrder()))
                        .toList();
            }
            return new GetOrdersOutput(orders.stream().map(OrderToGetOrderRsMapper::map).toList());
        }
        if ("COMPLETED_ORDER".equals(input.type()) && "COMPLETION_DATE".equals(input.field())) {
            List<Order> orders;
            if ("ASC".equals(input.direction())) {
                orders = orderRepo.findAll()
                        .stream()
                        .filter(Order::isCompleted)
                        .sorted(Comparator.comparing(Order::getCompletionDate))
                        .toList();
            } else {
                orders = orderRepo.findAll()
                        .stream()
                        .filter(Order::isCompleted)
                        .sorted(Comparator.comparing(Order::getCompletionDate,
                                Comparator.reverseOrder()))
                        .toList();
            }
            return new GetOrdersOutput(orders.stream().map(OrderToGetOrderRsMapper::map).toList());
        }
        if ("COMPLETED_ORDER".equals(input.type()) && "PRICE".equals(input.field())) {
            List<Order> orders;
            if ("ASC".equals(input.direction())) {
                orders = orderRepo.findAll()
                        .stream()
                        .filter(Order::isCompleted)
                        .sorted(Comparator.comparing(Order::getTotalPrice))
                        .toList();
            } else {
                orders = orderRepo.findAll()
                        .stream()
                        .filter(Order::isCompleted)
                        .sorted(Comparator.comparing(Order::getTotalPrice,
                                Comparator.reverseOrder()))
                        .toList();
            }
            return new GetOrdersOutput(orders.stream().map(OrderToGetOrderRsMapper::map).toList());
        }

        throw new IncompatibleSortTypesException("Incompatible types for sorting: "
                + input.type()
                + " and "
                + input.type() + ".");
    }
}
