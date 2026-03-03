package com.myapp.app.domain.entities.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private Long id;
    private Long customerId;
    private OrderStatus status;
    private List<OrderItem> items = new ArrayList<>();
    private LocalDateTime createdDate;
    private LocalDateTime completionDate;
    private BigDecimal totalPrice;

    public Order(Long customerId, List<OrderItem> items, OrderStatus status, LocalDateTime createdDate, BigDecimal totalPrice, LocalDateTime completionDate) {
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.status = status;
        this.createdDate = createdDate;
        this.totalPrice = totalPrice;
        this.completionDate = completionDate;
    }

    public static Order createNew(Long customerId, List<String> listBookIsbn) {
        if (null == customerId) {
            throw new IllegalArgumentException("customerId");
        }
        if (null == listBookIsbn) {
            throw new IllegalArgumentException("items");
        }
        return new Order(customerId,
                buildItemsFromIsbns(listBookIsbn),
                OrderStatus.NEW,
                LocalDateTime.now(),
                null,
                null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || getClass() != o.getClass()) {
            return false;
        }

        Order other = (Order) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public LocalDateTime getCompletionDate() {
        return this.completionDate;
    }


    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public boolean isCompleted() {
        return OrderStatus.COMPLETED == this.status;
    }

    public boolean isCanceled() {
        return OrderStatus.CANCELLED == this.status;
    }

    public void changeStatus(OrderStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new IllegalStateException(
                    "Нельзя сменить статус с " + status + " на " + target + "."
            );
        }
        this.status = target;
    }

    public void cancel() {
        this.changeStatus(OrderStatus.CANCELLED);
    }

    public void reserve(List<Long> reservedBookUnitId) {
        int n = Math.min(reservedBookUnitId.size(), this.items.size());
        for (int i = 0; i < n; i++) {
            this.items.get(i).setReservedBookUnitId(reservedBookUnitId.get(i));
        }
    }

    public void complete(LocalDateTime completionDate) {
        if (!this.areAllItemsReserved()) {
            throw new IllegalStateException(
                    "Заказ не может быть завершен, потому что некоторых книг нет на складе."
            );
        }
        this.changeStatus(OrderStatus.COMPLETED);
        this.completionDate = completionDate;
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
}
