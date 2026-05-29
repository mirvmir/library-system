package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.exception.UnauthorizedException;
import io.github.mirvmir.exception.business.IncompatibleSortTypesException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.services.interfaces.GetOrderService;
import io.github.mirvmir.useCases.services.mapping.OrderToGetOrderRsMapper;
import io.github.mirvmir.useCases.services.inputs.GetOrderInput;
import io.github.mirvmir.useCases.services.outputs.GetOrdersOutput;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
        Long currentUserId = null;
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            currentUserId = Long.valueOf(jwt.getSubject());
        }

        if ("ORDER".equals(input.type()) && "COMPLETION_DATE".equals(input.field())) {
            List<Order> orders = orderRepo.findAllByUserId(currentUserId)
                    .stream()
                    .sorted(Comparator.comparing(Order::getCompletionAt,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();
            return new GetOrdersOutput(orders.stream().map(OrderToGetOrderRsMapper::map).toList());
        }
        if ("ORDER".equals(input.type()) && "PRICE".equals(input.field())) {
            List<Order> orders;
            if ("ASC".equals(input.direction())) {
                orders = orderRepo.findAllByUserId(currentUserId)
                        .stream()
                        .sorted(Comparator.comparing(Order::getTotalPrice))
                        .toList();
            } else {
                orders = orderRepo.findAllByUserId(currentUserId)
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
                orders = orderRepo.findAllByUserId(currentUserId)
                        .stream()
                        .sorted(Comparator.comparing(Order::getStatus))
                        .toList();
            } else {
                orders = orderRepo.findAllByUserId(currentUserId)
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
                orders = orderRepo.findAllByUserId(currentUserId)
                        .stream()
                        .filter(Order::isCompleted)
                        .sorted(Comparator.comparing(Order::getCompletionAt))
                        .toList();
            } else {
                orders = orderRepo.findAllByUserId(currentUserId)
                        .stream()
                        .filter(Order::isCompleted)
                        .sorted(Comparator.comparing(Order::getCompletionAt,
                                Comparator.reverseOrder()))
                        .toList();
            }
            return new GetOrdersOutput(orders.stream().map(OrderToGetOrderRsMapper::map).toList());
        }
        if ("COMPLETED_ORDER".equals(input.type()) && "PRICE".equals(input.field())) {
            List<Order> orders;
            if ("ASC".equals(input.direction())) {
                orders = orderRepo.findAllByUserId(currentUserId)
                        .stream()
                        .filter(Order::isCompleted)
                        .sorted(Comparator.comparing(Order::getTotalPrice))
                        .toList();
            } else {
                orders = orderRepo.findAllByUserId(currentUserId)
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
