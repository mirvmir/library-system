package io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.user.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "basket")
public class BasketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "basket_book_models",
            joinColumns = @JoinColumn(name = "basket_id"),
            inverseJoinColumns = @JoinColumn(name = "book_model_id")
    )
    private List<BookModel> models = new ArrayList<>();

    protected BasketEntity() {
    }

    private BasketEntity(User user, List<BookModel> models) {
        this.user = user;
        this.models = models;
    }

    public static BasketEntity createNewBasket(User user) {
        return new BasketEntity(user, new ArrayList<>());
    }

    public void clean() {
        this.models.clear();
    }

    public void add(BookModel book) {
        this.models.add(book);
    }

    public void writeOff(String isbn) {
        BookModel book = models.stream()
                .filter(bookModel -> Objects.equals(bookModel.getIsbn(), isbn))
                .findFirst()
                .orElse(null);
        this.models.remove(book);
    }

    public List<BookModel> getModels() {
        return this.models;
    }
}