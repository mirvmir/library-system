package com.myapp.app.domain.entities.request;

public class BookRequest {

    private Long id;
    private String isbn;
    private Long customerId;

    public BookRequest(String isbn, Long customerId) {
        this.isbn = isbn;
        this.customerId = customerId;
    }

    public static BookRequest createNew(String isbn, Long customerId) {
        if (null == isbn || isbn.isBlank()) throw new IllegalArgumentException("isbn");
        return new BookRequest(isbn, customerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (null == o || getClass() != o.getClass()) return false;

        BookRequest other = (BookRequest) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
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

    public Long getCustomerId() {
        return customerId;
    }
}
