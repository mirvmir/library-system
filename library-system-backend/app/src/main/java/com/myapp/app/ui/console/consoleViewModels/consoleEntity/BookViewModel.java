package com.myapp.app.ui.console.consoleViewModels.consoleEntity;

public class BookViewModel {
    private final String isbn;
    private final String title;
    private final String author;
    private final String price;
    private final boolean available;

    public BookViewModel(String isbn, String title, String author, String price, boolean available) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.available = available;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }
}
