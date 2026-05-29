package io.github.mirvmir.domain.entities.order;

import io.github.mirvmir.exception.business.BusinessRuleViolation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private Long id;
    private Long customerId;
    private OrderStatus status;
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime completionAt;
    private LocalDateTime expiresAt;

    public Order(Long customerId,
                 List<OrderItem> items,
                 OrderStatus status,
                 BigDecimal totalPrice,
                 LocalDateTime createdAt,
                 LocalDateTime completionAt,
                 LocalDateTime expiresAt) {
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.completionAt = completionAt;
        this.expiresAt = expiresAt;
    }

    public static Order createNew(Long customerId,
                                  List<String> listBookIsbn,
                                  LocalDateTime now,
                                  long expiresMinutes) {
        if (customerId == null) {
            throw new BusinessRuleViolation("Order customerId is required.");
        }

        if (listBookIsbn == null || listBookIsbn.isEmpty()) {
            throw new BusinessRuleViolation("Order must contain at least one book.");
        }

        return new Order(
                customerId,
                buildItemsFromIsbns(listBookIsbn),
                OrderStatus.CREATED,
                null,
                now,
                null,
                now.plus(expiresMinutes, ChronoUnit.MINUTES)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Order other = (Order) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public void markPaymentProcessing(LocalDateTime now) {
        if (OrderStatus.PAYMENT_PROCESSING == status) {
            return;
        }

        if (OrderStatus.CREATED != status) {
            throw new BusinessRuleViolation("Order is already processed.");
        }

        if (!now.isBefore(expiresAt)) {
            throw new BusinessRuleViolation("Order payment time has expired.");
        }

        status = OrderStatus.PAYMENT_PROCESSING;
    }

    public void markPayed(LocalDateTime now) {
        if (OrderStatus.PAYED == status) {
            return;
        }

        if (OrderStatus.REFUND_REQUIRED == status || OrderStatus.REFUNDED == status) {
            return;
        }

        if (OrderStatus.CREATED != status && OrderStatus.PAYMENT_PROCESSING != status) {
            throw new BusinessRuleViolation("Order is already processed.");
        }

        if (!now.isBefore(expiresAt)) {
            throw new BusinessRuleViolation("Order payment time has expired.");
        }

        status = OrderStatus.PAYED;
    }

    public void complete(LocalDateTime completionAt) {
        if (OrderStatus.COMPLETED == status) {
            return;
        }

        if (OrderStatus.PAYED != status) {
            throw new BusinessRuleViolation("Only payed order can be completed.");
        }

        if (!areAllItemsReserved()) {
            throw new BusinessRuleViolation(
                    "Order cannot be completed because some books are not reserved."
            );
        }

        status = OrderStatus.COMPLETED;
        this.completionAt = completionAt;
    }

    public void cancel() {
        if (OrderStatus.CREATED == status || OrderStatus.PAYMENT_PROCESSING == status) {
            status = OrderStatus.CANCELLED;
            return;
        }

        if (OrderStatus.PAYED == status) {
            status = OrderStatus.REFUND_REQUIRED;
            return;
        }

        if (OrderStatus.CANCELLED == status
                || OrderStatus.REFUND_REQUIRED == status
                || OrderStatus.REFUNDED == status) {
            return;
        }

        throw new BusinessRuleViolation("Order cannot be cancelled.");
    }

    public void expire(LocalDateTime now) {
        if (OrderStatus.EXPIRED == status) {
            return;
        }

        if (OrderStatus.CREATED != status && OrderStatus.PAYMENT_PROCESSING != status) {
            throw new BusinessRuleViolation("Order cannot be expired.");
        }

        if (now.isBefore(expiresAt)) {
            throw new BusinessRuleViolation("Order is not expired yet.");
        }

        status = OrderStatus.EXPIRED;
    }

    public void markRefundRequired() {
        if (OrderStatus.REFUND_REQUIRED == status) {
            return;
        }

        if (OrderStatus.PAYED == status
                || OrderStatus.CANCELLED == status
                || OrderStatus.EXPIRED == status
                || OrderStatus.PAYMENT_PROCESSING == status) {
            status = OrderStatus.REFUND_REQUIRED;
            return;
        }

        throw new BusinessRuleViolation("Order cannot be refunded.");
    }

    public void markRefunded() {
        if (OrderStatus.REFUNDED == status) {
            return;
        }

        if (OrderStatus.REFUND_REQUIRED != status) {
            throw new BusinessRuleViolation("Order cannot be marked as refunded.");
        }

        status = OrderStatus.REFUNDED;
    }

    public void reserve(List<Long> reservedBookUnitId) {
        int n = Math.min(reservedBookUnitId.size(), items.size());

        for (int i = 0; i < n; i++) {
            items.get(i).setReservedBookUnitId(reservedBookUnitId.get(i));
        }
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(expiresAt);
    }

    public boolean isExpiredStatus() {
        return OrderStatus.EXPIRED == status;
    }

    public boolean isCancelled() {
        return OrderStatus.CANCELLED == status;
    }

    public boolean isPaymentProcessing() {
        return OrderStatus.PAYMENT_PROCESSING == status;
    }

    public boolean isPayed() {
        return OrderStatus.PAYED == status;
    }

    public boolean isCompleted() {
        return OrderStatus.COMPLETED == status;
    }

    public boolean isRefundRequired() {
        return OrderStatus.REFUND_REQUIRED == status;
    }

    public boolean isRefunded() {
        return OrderStatus.REFUNDED == status;
    }

    public boolean isFinalStatus() {
        return OrderStatus.COMPLETED == status
                || OrderStatus.CANCELLED == status
                || OrderStatus.REFUNDED == status
                || OrderStatus.EXPIRED == status;
    }

    private static List<OrderItem> buildItemsFromIsbns(List<String> isbns) {
        List<OrderItem> items = new ArrayList<>();

        for (String isbn : isbns) {
            items.add(OrderItem.createRequested(isbn));
        }

        return items;
    }

    private boolean areAllItemsReserved() {
        return items.stream().allMatch(OrderItem::isReserved);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletionAt() {
        return completionAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}