package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.controllers.web.requests.BankPaymentWebhookRq;
import io.github.mirvmir.controllers.web.requests.BankPayoutWebhookRq;
import io.github.mirvmir.controllers.web.requests.BankRefundWebhookRq;
import io.github.mirvmir.controllers.web.requests.BindCardRq;
import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.order.OrderItem;
import io.github.mirvmir.domain.entities.payment.*;
import io.github.mirvmir.event.EventPublisher;
import io.github.mirvmir.exception.UnauthorizedException;
import io.github.mirvmir.exception.business.*;
import io.github.mirvmir.exception.integration.CardBindingException;
import io.github.mirvmir.exception.integration.PaymentException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.frameworks.integration.BankPaymentGatewayClient;
import io.github.mirvmir.frameworks.integration.outputs.BankBindCardOutput;
import io.github.mirvmir.frameworks.integration.outputs.BankBindCardRq;
import io.github.mirvmir.frameworks.integration.outputs.BankPayOutput;
import io.github.mirvmir.frameworks.integration.outputs.BankPayRq;
import io.github.mirvmir.useCases.adapter.integration.payment.event.OrderExpiredEvent;
import io.github.mirvmir.useCases.adapter.integration.payment.event.OrderPayedEvent;
import io.github.mirvmir.useCases.adapter.integration.payment.event.OrderRefundedEvent;
import io.github.mirvmir.useCases.adapter.integration.payment.event.PayoutSucceededEvent;
import io.github.mirvmir.useCases.adapter.repository.interfaces.*;
import io.github.mirvmir.useCases.services.interfaces.PaymentService;
import io.github.mirvmir.useCases.services.outputs.BindCardOutput;
import io.github.mirvmir.useCases.services.outputs.ConfirmPaymentOutput;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DefaultPaymentService implements PaymentService {

    private static final BigDecimal CARD_VERIFICATION_AMOUNT = BigDecimal.ONE;

    private final Config config;

    private final BankPaymentGatewayClient bankPaymentGatewayClient;

    private final UserCardRepository userCardRepository;
    private final PaymentRepository paymentRepository;
    private final PayoutRepository payoutRepository;
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;

    private final EventPublisher eventPublisher;

    private final Clock clock;

    public DefaultPaymentService(
            Config config,
            BankPaymentGatewayClient bankPaymentGatewayClient,
            UserCardRepository userCardRepository,
            PaymentRepository paymentRepository,
            PayoutRepository payoutRepository,
            RefundRepository refundRepository,
            OrderRepository orderRepository, EventPublisher eventPublisher,
            Clock clock
    ) {
        this.config = config;
        this.bankPaymentGatewayClient = bankPaymentGatewayClient;
        this.userCardRepository = userCardRepository;
        this.paymentRepository = paymentRepository;
        this.payoutRepository = payoutRepository;
        this.refundRepository = refundRepository;
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    @Override
    @Transactional
    public BindCardOutput bindCard(BindCardRq request) {
        bankPaymentGatewayClient.checkAvailability();

        BankBindCardRq bankRequest = new BankBindCardRq(
                request.cardNumber(),
                request.cardHolder(),
                request.expiryMonth(),
                request.expiryYear(),
                request.cvc(),
                CARD_VERIFICATION_AMOUNT,
                UUID.randomUUID().toString()
        );

        BankBindCardOutput bankResponse =
                bankPaymentGatewayClient.bindCard(bankRequest);

        if (!"SUCCEEDED".equals(bankResponse.status())) {
            throw new CardBindingException("Не удалось подключить карту.");
        }

        Long currentUserId = getCurrentUserId();

        if (currentUserId == null) {
            throw new UnauthorizedException("User not authorized.");
        }

        if (userCardRepository.existsByUserIdAndCardToken(
                currentUserId,
                bankResponse.cardToken()
        )) {
            throw new CardBindingException("Карта уже подключена.");
        }

        boolean isFirstCard = !userCardRepository.existsByUserId(
                currentUserId
        );

        UserCard card = UserCard.createBoundCard(
                currentUserId,
                bankResponse.bankCardId(),
                bankResponse.cardToken(),
                bankResponse.maskedPan(),
                bankResponse.last4(),
                bankResponse.paymentSystem(),
                isFirstCard,
                LocalDateTime.now(clock)
        );

        UserCard savedCard = userCardRepository.save(card);

        return new BindCardOutput(
                savedCard.getId(),
                savedCard.getMaskedPan(),
                savedCard.getPaymentSystem()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BindCardOutput> getMyCard() {
        Long currentUserId = getCurrentUserId();

        if (currentUserId == null) {
            throw new UnauthorizedException("User not authorized.");
        }

        List<UserCard> cards = userCardRepository.findByUserId(currentUserId);

        return cards.stream()
                .map(card ->
                        new BindCardOutput(
                                card.getId(),
                                card.getMaskedPan(),
                                card.getPaymentSystem()
                        )
                ).toList();
    }

    @Override
    @Transactional
    public void setDefaultCard(Long cardId) {
        Long currentUserId = getCurrentUserId();

        if (currentUserId == null) {
            throw new UnauthorizedException("User not authorized.");
        }

        UserCard newDefaultCard = userCardRepository.findById(cardId);

        if (newDefaultCard == null || !currentUserId.equals(newDefaultCard.getUserId())) {
            throw new CardNotFoundException("Card with ID " + cardId + " not found.");
        }

        if (newDefaultCard.isDefaultCard()) {
            return;
        }

        UserCard oldDefaultCard =
                userCardRepository.findDefaultByUserId(currentUserId);

        if (oldDefaultCard != null) {
            oldDefaultCard.unmarkAsDefault();
            userCardRepository.save(oldDefaultCard);
        }

        newDefaultCard.markAsDefault();
        userCardRepository.save(newDefaultCard);
    }

    @Override
    @Transactional
    public ConfirmPaymentOutput startPayment(Long paymentId, Long cardId) {
        Payment payment = paymentRepository.findById(paymentId);

        if (payment == null) {
            throw new PaymentNotFoundException(
                    "Payment with ID " + paymentId + " not found."
            );
        }

        if (PaymentStatus.SUCCEEDED == payment.getStatus()
                || PaymentStatus.PROCESSING == payment.getStatus()) {
            return new ConfirmPaymentOutput(
                    payment.getId(),
                    payment.getStatus().name()
            );
        }

        if (PaymentStatus.CANCELLED == payment.getStatus()) {
            throw new PaymentException("Платёж отменён.");
        }

        if (PaymentStatus.CREATED != payment.getStatus()) {
            throw new PaymentException("Платёж уже обработан.");
        }

        LocalDateTime orderNow = LocalDateTime.now(clock);

        Order order = orderRepository.findByIdForUpdate(payment.getOrderId());

        if (order == null) {
            throw new OrderNotFoundException(
                    "Order with ID " + payment.getOrderId() + " not found."
            );
        }

        if (order.isCancelled()) {
            payment.cancel();
            paymentRepository.save(payment);

            throw new PaymentException("Заказ был отменён.");
        }

        if (order.isExpired(orderNow)) {
            order.expire(orderNow);
            payment.expire();

            orderRepository.save(order);
            paymentRepository.save(payment);

            eventPublisher.publishOrderExpired(
                    new OrderExpiredEvent(
                            order.getId(),
                            order.getCustomerId(),
                            order.getTotalPrice(),
                            order.getItems()
                                    .stream()
                                    .map(OrderItem::getBookIsbn)
                                    .toList(),
                            order.getCreatedAt(),
                            order.getExpiresAt(),
                            orderNow
                    )
            );

            throw new PaymentException("Время оплаты истекло.");
        }

        order.markPaymentProcessing(orderNow);
        orderRepository.save(order);

        UserCard card = cardId != null
                ? userCardRepository.findById(cardId)
                : userCardRepository.findDefaultByUserId(payment.getUserId());

        if (card == null
                || !card.getUserId().equals(payment.getUserId())
                || !card.isActive()) {
            throw new PaymentNotFoundException(
                    "Card for payment with ID " + paymentId + " not found."
            );
        }

        bankPaymentGatewayClient.checkAvailability();

        BankPayOutput bankResponse = bankPaymentGatewayClient.pay(
                new BankPayRq(
                        card.getCardToken(),
                        payment.getPrice(),
                        String.valueOf(payment.getOrderId()),
                        payment.getDescription(),
                        config.getPaymentWebhookUrl()
                )
        );

        if ("PROCESSING".equals(bankResponse.status())) {
            payment.markProcessing(bankResponse.paymentId());
        } else if ("FAILED".equals(bankResponse.status())) {
            payment.markFailed(bankResponse.paymentId());
        } else {
            throw new PaymentException("Некорректный статус платежа от банка.");
        }

        paymentRepository.save(payment);

        return new ConfirmPaymentOutput(
                payment.getId(),
                payment.getStatus().name()
        );
    }

    @Override
    @Transactional
    public void handleBankWebhook(BankPaymentWebhookRq request) {
        Payment payment = paymentRepository.findByExternalPaymentId(
                request.externalPaymentId()
        );

        if (payment == null) {
            throw new PaymentNotFoundException(
                    "Payment with ID " + request.externalPaymentId() + " not found."
            );
        }

        PaymentStatus bankStatus = PaymentStatus.valueOf(request.status());
        LocalDateTime now = LocalDateTime.now(clock);

        if (PaymentStatus.SUCCEEDED == bankStatus) {
            payment.markSucceededFromWebhook(now);

            Order order = orderRepository.findByIdForUpdate(payment.getOrderId());

            if (order == null) {
                throw new OrderNotFoundException(
                        "Order with ID " + payment.getOrderId() + " not found."
                );
            }

            LocalDateTime orderNow = LocalDateTime.now(clock);

            if (order.isCancelled()
                    || order.isExpiredStatus()
                    || order.isRefundRequired()
                    || order.isRefunded()) {
                order.markRefundRequired();
                orderRepository.save(order);

                paymentRepository.save(payment);

                eventPublisher.publishOrderPayed(
                        new OrderPayedEvent(
                                order.getId(),
                                order.getCustomerId(),
                                order.getTotalPrice(),
                                orderNow
                        )
                );

                return;
            }

            order.markPayed(orderNow);

            orderRepository.save(order);

            eventPublisher.publishOrderPayed(
                    new OrderPayedEvent(
                            order.getId(),
                            order.getCustomerId(),
                            order.getTotalPrice(),
                            orderNow
                    )
            );
        } else if (PaymentStatus.FAILED == bankStatus) {
            payment.markFailedFromWebhook();
        } else {
            throw new PaymentException("Некорректный статус платежа от банка.");
        }

        paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void handleBankPayoutWebhook(BankPayoutWebhookRq request) {
        Payout payout = payoutRepository.findByExternalPayoutId(
                request.externalPayoutId()
        );

        if (payout == null) {
            throw new PayoutNotFoundException(
                    "Payout with ID " + request.externalPayoutId() + " not found."
            );
        }

        PayoutStatus bankStatus = PayoutStatus.valueOf(request.status());

        if (PayoutStatus.SUCCEEDED == bankStatus) {
            payout.markSucceededFromWebhook(
                    LocalDateTime.now(clock)
            );

            eventPublisher.publishPayoutSucceeded(
                    new PayoutSucceededEvent(
                            payout.getId(),
                            payout.getWalletWithdrawalId(),
                            payout.getUserId(),
                            payout.getPrice(),
                            payout.getPaidAt()
                    )
            );
        } else if (PayoutStatus.FAILED == bankStatus) {
            payout.markFailedFromWebhook();
        } else {
            throw new PaymentException("Некорректный статус вывода от банка");
        }

        payoutRepository.saveOrUpdate(payout);
    }

    @Override
    @Transactional
    public void handleBankRefundWebhook(BankRefundWebhookRq request) {
        Refund refund = refundRepository.findByExternalRefundId(
                request.externalRefundId()
        );

        if (refund == null) {
            throw new RefundNotFoundException(
                    "Refund with ID " + request.externalRefundId() + " not found."
            );
        }

        if (refund.isSucceeded()) {
            return;
        }

        RefundStatus bankStatus = RefundStatus.valueOf(request.status());
        LocalDateTime now = LocalDateTime.now(clock);

        if (RefundStatus.SUCCEEDED == bankStatus) {
            refund.markSucceededFromWebhook(now);

            Payment payment = paymentRepository.findById(
                    refund.getPaymentId()
            );

            if (payment == null) {
                throw new PaymentNotFoundException(
                        "Payment with ID " + refund.getPaymentId() + " not found."
                );
            }

            Order order = orderRepository.findByIdForUpdate(payment.getOrderId());

            if (order == null) {
                throw new OrderNotFoundException(
                        "Order with ID " + payment.getOrderId() + " not found."
                );
            }

            order.markRefunded();

            orderRepository.save(order);

            eventPublisher.publishOrderRefunded(
                    new OrderRefundedEvent(
                            order.getId(),
                            order.getCustomerId(),
                            order.getTotalPrice(),
                            now
                    )
            );
        } else if (RefundStatus.FAILED == bankStatus) {
            refund.markFailedFromWebhook();
        } else {
            throw new PaymentException("Некорректный статус возврата от банка");
        }

        refundRepository.saveOrUpdate(refund);
    }

    private Long getCurrentUserId() {
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

        return currentUserId;
    }
}
