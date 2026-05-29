package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.bookUnit.BookUnit;
import io.github.mirvmir.exception.business.BookNotFoundException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookRequestRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookUnitRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultAddBookToStockService;
import io.github.mirvmir.useCases.services.inputs.AddBookToStockInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class DefaultAddBookToStockServiceTest {
    private Config config;
    private BookUnitRepository unitRepo;
    private BookModelRepository modelRepo;
    private BookRequestRepository requestRepo;

    private DefaultAddBookToStockService service;

    @BeforeEach
    void setUp() {
        config = mock(Config.class);
        unitRepo = mock(BookUnitRepository.class);
        modelRepo = mock(BookModelRepository.class);
        requestRepo = mock(BookRequestRepository.class);

        service = new DefaultAddBookToStockService(config, unitRepo, modelRepo, requestRepo);
    }

    @Test
    void execute_shouldAddBook_withAutoCompleteRequest() {
        String isbn = "123";

        when(config.isAutoCompleteRequests())
                .thenReturn(true);

        when(unitRepo.save(any()))
                .thenAnswer(inv -> {
                    BookUnit req = inv.getArgument(0);
                    req.setId(2L);
                    return req;
                });

        BookModel modelForAddToStock = new BookModel(
                isbn,
                "Преступление и наказание",
                "Федор Михайлович Достоевский",
                new BigDecimal("345"),
                0,
                2
        );
        int oldStockCount = modelForAddToStock.getStockCount();
        when(modelRepo.findByIsbn(isbn)).thenReturn(modelForAddToStock);

        service.execute(new AddBookToStockInput(isbn));

        verify(requestRepo).deleteByIsbn(isbn);
        verify(modelRepo).update(modelForAddToStock);

        assertEquals(0, modelForAddToStock.getRequestCount());
        assertEquals(oldStockCount + 1, modelForAddToStock.getStockCount());
    }

    @Test
    void execute_shouldAddBook_withoutAutoCompleteRequest() {
        String isbn = "123";

        when(config.isAutoCompleteRequests())
                .thenReturn(false);

        when(unitRepo.save(any()))
                .thenAnswer(inv -> {
                    BookUnit req = inv.getArgument(0);
                    req.setId(2L);
                    return req;
                });

        BookModel modelForAddToStock = new BookModel(
                isbn,
                "Преступление и наказание",
                "Федор Михайлович Достоевский",
                new BigDecimal("345"),
                0,
                2
        );
        int oldStockCount = modelForAddToStock.getStockCount();
        int oldRequestCount = modelForAddToStock.getRequestCount();
        when(modelRepo.findByIsbn(isbn))
                .thenReturn(modelForAddToStock);

        service.execute(new AddBookToStockInput(isbn));

        verify(requestRepo, never()).deleteByIsbn(isbn);
        verify(modelRepo).update(modelForAddToStock);

        assertEquals(oldRequestCount, modelForAddToStock.getRequestCount());
        assertEquals(oldStockCount + 1, modelForAddToStock.getStockCount());
    }

    @Test
    void execute_shouldNotAddBook_shouldThrowException_whenBookModelNotFound() {
        String isbn = "123";

        when(modelRepo.findByIsbn(isbn)).thenReturn(null);

        BookNotFoundException exception = assertThrows(
                BookNotFoundException.class,
                () -> service.execute(new AddBookToStockInput(isbn))
        );
        assertEquals("Book with ISBN " + isbn + " not found.", exception.getMessage());

        verify(modelRepo).findByIsbn(isbn);
        verify(unitRepo, never()).save(any(BookUnit.class));
        verify(requestRepo, never()).deleteByIsbn(anyString());
        verify(modelRepo, never()).update(any(BookModel.class));
    }
}
