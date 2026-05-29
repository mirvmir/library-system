package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.request.BookRequest;
import io.github.mirvmir.exception.business.BookNotFoundException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookRequestRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultCreateBookRequestService;
import io.github.mirvmir.useCases.services.inputs.CreateBookRequestInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class DefaultCreateBookRequestServiceTest {
    private BookRequestRepository requestRepo;
    private BookModelRepository modelRepo;

    private DefaultCreateBookRequestService service;

    @BeforeEach
    void setUp() {
        requestRepo = mock(BookRequestRepository.class);
        modelRepo = mock(BookModelRepository.class);

        service = new DefaultCreateBookRequestService(requestRepo, modelRepo);
    }

    @Test
    void execute_shouldCreateBookRequest() {
        CreateBookRequestInput input = new CreateBookRequestInput("123", 1L);

        when(requestRepo.save(any()))
                .thenAnswer(inv -> {
                    BookRequest req = inv.getArgument(0);
                    req.setId(2L);
                    return req;
                });

        BookModel bookForRequest = new BookModel(
                input.isbn(),
                "Преступление и наказание",
                "Федор Михайлович Достоевский",
                new BigDecimal("345"),
                0,
                2
        );
        int oldRequestCount = bookForRequest.getRequestCount();
        when(modelRepo.findByIsbn(input.isbn()))
                .thenReturn(bookForRequest);

        service.execute(input);

        verify(modelRepo).update(bookForRequest);

        ArgumentCaptor<BookRequest> captor = ArgumentCaptor.forClass(BookRequest.class);
        verify(requestRepo).save(captor.capture());
        BookRequest newRequest = captor.getValue();
        assertEquals(input.customerId(), newRequest.getCustomerId());
        assertEquals(input.isbn(), newRequest.getIsbn());

        assertEquals(oldRequestCount + 1, bookForRequest.getRequestCount());
    }

    @Test
    void execute_shouldNotCreateBookRequest_shouldThrowException_whenBookNotFound() {
        CreateBookRequestInput input = new CreateBookRequestInput("123", 1L);

        when(modelRepo.findByIsbn(input.isbn()))
                .thenReturn(null);

        BookNotFoundException exception = assertThrows(
                BookNotFoundException.class,
                () -> service.execute(input)
        );
        assertEquals("Book with ISBN " + input.isbn() + " not found.", exception.getMessage());

        verify(modelRepo, never()).update(any(BookModel.class));
        verify(requestRepo, never()).save(any(BookRequest.class));
    }
}
