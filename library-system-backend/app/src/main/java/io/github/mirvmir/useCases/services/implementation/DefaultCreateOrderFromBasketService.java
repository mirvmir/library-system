package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.bookUnit.BookUnit;
import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.payment.Payment;
import io.github.mirvmir.domain.entities.request.BookRequest;
import io.github.mirvmir.exception.UnauthorizedException;
import io.github.mirvmir.exception.business.BusinessException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.BasketEntity;
import io.github.mirvmir.useCases.adapter.repository.interfaces.*;
import io.github.mirvmir.useCases.services.interfaces.CreateOrderFromBasketService;
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
public class DefaultCreateOrderFromBasketService implements CreateOrderFromBasketService {

    private final OrderRepository orderRepo;
    private final BookModelRepository modelRepo;
    private final BookRequestRepository requestRepo;
    private final BasketRepository basketRepo;
    private final BookUnitRepository unitRepo;
    private final PaymentRepository paymentRepo;

    private final Config config;
    private final Clock clock;

    public DefaultCreateOrderFromBasketService(OrderRepository orderRepo,
                                               BookModelRepository modelRepo,
                                               BookRequestRepository requestRepo,
                                               BasketRepository basketRepo,
                                               BookUnitRepository unitRepo,
                                               PaymentRepository paymentRepo,
                                               Config config,
                                               Clock clock) {
        this.orderRepo = orderRepo;
        this.modelRepo = modelRepo;
        this.requestRepo = requestRepo;
        this.basketRepo = basketRepo;
        this.unitRepo = unitRepo;
        this.paymentRepo = paymentRepo;
        this.config = config;
        this.clock = clock;
    }

    @Override
    @Transactional
    public CreateOrderOutput execute() {
        Long currentUserId = getCurrentUserId();

        BasketEntity basket = basketRepo.findByUserId(currentUserId);

        if (basket == null || basket.getModels().isEmpty()) {
            throw new BusinessException("Корзина пуста.");
        }

        List<String> isbns = basket.getModels()
                .stream()
                .map(BookModel::getIsbn)
                .toList();

        Set<String> unavailable = modelRepo.findUnavailableIsbns(isbns);

        if (!unavailable.isEmpty()) {
            List<BookRequest> requests = unavailable.stream()
                    .map(isbn -> BookRequest.createNew(isbn, currentUserId))
                    .toList();

            requestRepo.saveAll(requests);

            throw new BusinessException("В корзине есть недоступные книги.");
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

        basket.clean();

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