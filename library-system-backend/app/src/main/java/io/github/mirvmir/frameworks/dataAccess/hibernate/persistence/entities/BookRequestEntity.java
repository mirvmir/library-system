package io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.user.User;

import jakarta.persistence.*;

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
    @JoinColumn(name = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public BookModel getBookModel() {
        return bookModel;
    }

    public User getCustomer() {
        return user;
    }

    public void setBookModel(BookModel bookModel) {
        this.bookModel = bookModel;
    }

    public void setCustomer(User user) {
        this.user = user;
    }
}
