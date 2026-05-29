package io.github.mirvmir.domain.entities.bookUnit;

import io.github.mirvmir.exception.business.BusinessRuleViolation;

import java.time.LocalDate;

public class BookUnit {

    private Long id;
    private String isbn;
    private LocalDate deliveryDate;
    private boolean available;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (null == o || getClass() != o.getClass()) return false;

        BookUnit other = (BookUnit) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    public BookUnit(
            String isbn,
            boolean available,
            LocalDate deliveryDate) {
        this.isbn = isbn;
        this.available = available;
        this.deliveryDate = deliveryDate;
    }

    public static BookUnit createNew(String isbn) {
        if (null == isbn || isbn.isBlank()) {
            throw new BusinessRuleViolation("Book isbn is required.");
        }
        return new BookUnit(isbn, true, LocalDate.now());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public boolean isAvailable() {
        return available;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void writeOff() {
        if (!available) {
            throw new BusinessRuleViolation("No available copies for book.");
        }
        this.available = false;
    }
}