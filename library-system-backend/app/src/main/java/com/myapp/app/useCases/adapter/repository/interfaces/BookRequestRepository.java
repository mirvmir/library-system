package com.myapp.app.useCases.adapter.repository.interfaces;

import com.myapp.app.domain.entities.request.BookRequest;

import java.util.List;

public interface BookRequestRepository {
    BookRequest save(BookRequest bookRequest);
    void deleteByIsbn(String isbn);
    void saveAll(List<BookRequest> requests);
}