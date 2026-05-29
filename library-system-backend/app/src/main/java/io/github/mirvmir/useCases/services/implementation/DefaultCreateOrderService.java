package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookUnit.BookUnit;
import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.payment.Payment;
import io.github.mirvmir.domain.entities.request.BookRequest;
import io.github.mirvmir.exception.UnauthorizedException;
import io.github.mirvmir.exception.business.BusinessException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.*;
import io.github.mirvmir.useCases.services.interfaces.CreateOrderService;
import io.github.mirvmir.useCases.services.inputs.CreateOrderInput;
import io.github.mirvmir.useCases.services.outputs.CreateOrderOutput;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class DefaultCreateOrderService implements CreateOrderService {

    private final OrderRepository orderRepo;
    private final BookModelRepository modelRepo;
    private final BookRequestRepository requestRepo;
    private final BookUnitRepository unitRepo;
    private final PaymentRepository paymentRepo;

    private final Config config;
    private final Clock clock;

    public DefaultCreateOrderService(OrderRepository orderRepo,
                                     BookModelRepository modelRepo,
                                     BookRequestRepository requestRepo,
                                     BookUnitRepository unitRepo,
                                     PaymentRepository paymentRepo,
                                     Config config,
                                     Clock clock) {
        this.orderRepo = orderRepo;
        this.modelRepo = modelRepo;
        this.requestRepo = requestRepo;
        this.unitRepo = unitRepo;
        this.paymentRepo = paymentRepo;
        this.config = config;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CreateOrderOutput execute(CreateOrderInput input) {
        Long currentUserId = getCurrentUserId();

        List<String> isbns = List.of(input.isbn());

        Set<String> unavailable = modelRepo.findUnavailableIsbns(isbns);

        if (unavailable.contains(input.isbn())) {
            requestRepo.save(BookRequest.createNew(input.isbn(), currentUserId));
            throw new BusinessException("Книга недоступна для покупки.");
        }

        List<Long> reservedBookUnitIds = new ArrayList<>();

        for (String isbn : isbns) {
            BookUnit bookUnit = unitRepo.findByIsbnForUpdate(isbn);

            if (bookUnit == null) {
                throw new BusinessException("Не удалось зарезервировать все книги.");
            }

            reservedBookUnitIds.add(bookUnit.getId());
        }

        unitRepo.markSold(reservedBookUnitIds);

        LocalDateTime now = LocalDateTime.now(clock);

        Order order = Order.createNew(
                currentUserId,
                isbns,
                now,
                config.getBookingExpiresMinutes()
        );

        order.reserve(reservedBookUnitIds);

        Order savedOrder = orderRepo.save(order);

        Payment payment = Payment.create(
                currentUserId,
                savedOrder.getTotalPrice(),
                "Оплата заказа №" + savedOrder.getId(),
                savedOrder.getId(),
                now
        );

        Payment savedPayment = paymentRepo.save(payment);

        return new CreateOrderOutput(
                savedOrder.getId(),
                savedPayment.getId(),
                savedOrder.getStatus().name(),
                savedPayment.getStatus().name()
        );
    }

    private Long getCurrentUserId() {
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
            return Long.valueOf(jwt.getSubject());
        }

        throw new UnauthorizedException("User is not authenticated.");
    }
}