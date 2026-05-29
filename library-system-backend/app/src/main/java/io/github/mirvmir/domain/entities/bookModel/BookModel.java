package io.github.mirvmir.domain.entities.bookModel;

import io.github.mirvmir.exception.business.BusinessRuleViolation;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "book_model")
public class BookModel {

    @Id
    private String isbn;
    private String title;
    private String author;
    private BigDecimal price;
    @Column(name = "stock_count")
    private Integer stockCount;
    @Column(name = "request_count")
    private Integer requestCount;

    protected BookModel() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (null == o || getClass() != o.getClass()) {
            return false;
        }

        BookModel other = (BookModel) o;
        return isbn != null && isbn.equals(other.isbn);
    }

    @Override
    public int hashCode() {
        return isbn.hashCode();
    }

    public BookModel(String isbn,
                     String title,
                     String author,
                     BigDecimal price,
                     Integer stockCount, Integer requestCount) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stockCount = stockCount;
        this.requestCount = requestCount;
    }

    public static BookModel createNew(String isbn,
                                      String title,
                                      String author,
                                      BigDecimal price) {
        if (null == isbn || isbn.isBlank()) {
            throw new BusinessRuleViolation("Book isbn is required.");
        }
        if (null == title || title.isBlank()) {
            throw new BusinessRuleViolation("Book title is required.");
        }
        if (null == author || author.isBlank()) {
            throw new BusinessRuleViolation("Book author is required.");
        }
        if (null == price || 0 >= price.compareTo(BigDecimal.ZERO)) {
            throw new BusinessRuleViolation("Book price is required and must be greater than 0.");
        }
        return new BookModel(isbn, title, author, price, 0, 0);
    }

    public void addBookUnit() {
        this.stockCount++;
    }

    public void writeOffBookUnit() {
        if (this.stockCount != 0) {
            this.stockCount--;
        } else {
            throw new BusinessRuleViolation("No available copies for book.");
        }
    }

    public void addBookRequest() {
        this.requestCount++;
    }

    public void removeBookRequest() {
        this.requestCount = 0;
    }

    public String getIsbn() {
        return this.isbn;
    }

    public String getTitle() {
        return this.title;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getAuthor() {
        return this.author;
    }

    public boolean isAvailable() {
        return this.stockCount > 0;
    }

    public boolean isHaveRequest() {
        return this.requestCount > 0;
    }

    public Integer getRequestCount() {
        return this.requestCount;
    }

    public Integer getStockCount() {
        return this.stockCount;
    }
}
