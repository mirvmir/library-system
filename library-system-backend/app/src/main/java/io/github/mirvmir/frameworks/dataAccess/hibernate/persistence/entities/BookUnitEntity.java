package io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities;

import io.github.mirvmir.domain.entities.bookModel.BookModel;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "book_unit")
public class BookUnitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "isbn")
    private BookModel bookModel;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    private boolean available;

    public Long getId() {
        return id;
    }

    public BookModel getBookModel() {
        return bookModel;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setBookModel(BookModel bookModel) {
        this.bookModel = bookModel;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
