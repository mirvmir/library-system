package io.github.mirvmir.domain.entities.order;

public class OrderItem {

    private Long id;

    private Long reservedBookUnitId;

    private final String bookIsbn;

    public OrderItem(Long bookUnitId, String booksIsbn) {
        this.reservedBookUnitId = bookUnitId;
        this.bookIsbn = booksIsbn;
    }

    public static OrderItem createRequested(String isbn) {
        return new OrderItem(null, isbn);
    }

    public Long getId() {
        return this.id;
    }

    public Long getBookId() {
        return reservedBookUnitId;
    }

    public void setReservedBookUnitId(Long reservedBookUnitId) {
        this.reservedBookUnitId = reservedBookUnitId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public boolean isReserved() {
        return reservedBookUnitId != null;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
