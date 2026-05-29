package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.domain.entities.bookModel.BookModel;

import java.util.List;
import java.util.Set;

public interface BookModelRepository {
    BookModel save(BookModel bookModel);
    List<BookModel> findAll();
    BookModel findByIsbn(String isbn);
    void update(BookModel bookModel);
    Set<String> findUnavailableIsbns(List<String> isbns);
}
