package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.domain.entities.request.BookRequest;

import java.util.List;

public interface BookRequestRepository {
    BookRequest save(BookRequest bookRequest);
    void deleteByIsbn(String isbn);
    void saveAll(List<BookRequest> requests);
}