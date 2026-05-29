package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.order.OrderItem;
import io.github.mirvmir.exception.UnauthorizedException;
import io.github.mirvmir.exception.business.OrderNotFoundException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.services.inputs.GetOrderDescriptionInput;
import io.github.mirvmir.useCases.services.interfaces.GetOrderDescriptionService;
import io.github.mirvmir.useCases.services.outputs.OrderDescriptionOutput;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultGetOrderDescriptionService implements GetOrderDescriptionService {

    private final OrderRepository orderRepo;

    public DefaultGetOrderDescriptionService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    @Transactional
    public OrderDescriptionOutput execute(GetOrderDescriptionInput input) {
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

        Order order = orderRepo.findById(input.orderId());

        if (order == null || !order.getCustomerId().equals(currentUserId)) {
            throw new OrderNotFoundException("Order with ID " + input.orderId() + " not found.");
        }

        return new OrderDescriptionOutput(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().toString(),
                order.getCompletionAt(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                order.getItems().stream().map(OrderItem::getBookIsbn).toList());
    }
}
