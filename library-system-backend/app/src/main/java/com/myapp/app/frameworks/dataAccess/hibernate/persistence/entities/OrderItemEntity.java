package com.myapp.app.frameworks.dataAccess.hibernate.persistence.entities;

import com.myapp.app.domain.entities.bookModel.BookModel;

import javax.persistence.*;

@Entity
@Table(name = "book_lists")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "book_unit_id")
    private Long reservedBookUnitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "isbn", referencedColumnName = "isbn")
    private BookModel bookModel;

    protected OrderItemEntity() {
    }

    public OrderItemEntity(Long bookUnitId, BookModel bookModel) {
        this.reservedBookUnitId = bookUnitId;
        this.bookModel = bookModel;
    }

    public Long getId() {
        return this.id;
    }

    public Long getBookId() {
        return reservedBookUnitId;
    }

    public BookModel getBookModel() {
        return bookModel;
    }
}
