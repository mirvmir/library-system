package com.myapp.app.frameworks.dataAccess.hibernate.persistence.entities;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.domain.entities.customer.Customer;

import javax.persistence.*;

@Entity
@Table(name = "book_request")
public class BookRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "isbn")
    private BookModel bookModel;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public Long getId() {
        return id;
    }

    public BookModel getBookModel() {
        return bookModel;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setBookModel(BookModel bookModel) {
        this.bookModel = bookModel;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
