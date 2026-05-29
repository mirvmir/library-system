package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.bookUnit.BookUnit;
import io.github.mirvmir.exception.business.BookNotFoundException;
import io.github.mirvmir.exception.business.BusinessRuleViolation;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookUnitRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultWriteOffBookService;
import io.github.mirvmir.useCases.services.inputs.WriteOffBookInput;
import io.github.mirvmir.useCases.services.outputs.WriteOffBookOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DefaultWriteOffBookServiceTest {
    private BookUnitRepository unitRepo;
    private BookModelRepository modelRepo;

    private DefaultWriteOffBookService service;

    @BeforeEach
    void setUp() {
        unitRepo = mock(BookUnitRepository.class);
        modelRepo = mock(BookModelRepository.class);

        service = new DefaultWriteOffBookService(unitRepo, modelRepo);
    }

    @Test
    void execute_shouldWriteOffBook() {
        String isbn = "123";
        WriteOffBookInput input = new WriteOffBookInput(isbn);

        BookModel bookModel = new BookModel(
                isbn,
                "Преступление и наказание",
                "Федор Михайлович Достоевский",
                new BigDecimal("345"),
                2,
                0
        );
        BookUnit bookUnit = new BookUnit(
                bookModel.getIsbn(),
                true,
                LocalDate.now()
        );
        bookUnit.setId(1L);
        int stockBefore = bookModel.getStockCount();
        when(modelRepo.findByIsbn(isbn))
                .thenReturn(bookModel);
        when(unitRepo.findByIsbn(bookModel))
                .thenReturn(bookUnit);

        WriteOffBookOutput result = service.execute(input);

        assertEquals(isbn, result.isbn());
        assertEquals(stockBefore - 1, bookModel.getStockCount());
        assertFalse(bookUnit.isAvailable());

        verify(modelRepo).findByIsbn(isbn);
        verify(modelRepo).update(bookModel);
        verify(unitRepo).findByIsbn(bookModel);
        verify(unitRepo).update(bookUnit);
    }

    @Test
    void execute_shouldNotWriteOffBook_shouldThrowException_whenBookModelNotFound() {
        String isbn = "123";
        WriteOffBookInput input = new WriteOffBookInput(isbn);

        when(modelRepo.findByIsbn(isbn))
                .thenReturn(null);

        BookNotFoundException ex = assertThrows(
                BookNotFoundException.class,
                () -> service.execute(input)
        );
        assertEquals("Book with ISBN " + isbn + " not found.", ex.getMessage());

        verify(modelRepo).findByIsbn(isbn);
        verify(modelRepo, never()).update(any());
        verifyNoInteractions(unitRepo);
    }

    @Test
    void execute_shouldNotWriteOffBook_shouldThrowException_whenStockCountIsZero() {
        String isbn = "123";
        WriteOffBookInput input = new WriteOffBookInput(isbn);

        BookModel bookModel = new BookModel(
                isbn,
                "Преступление и наказание",
                "Федор Михайлович Достоевский",
                new BigDecimal("345"),
                0,
                0
        );
        when(modelRepo.findByIsbn(isbn))
                .thenReturn(bookModel);

        BusinessRuleViolation ex = assertThrows(
                BusinessRuleViolation.class,
                () -> service.execute(input)
        );
        assertEquals("No available copies for book.", ex.getMessage());

        assertEquals(0, bookModel.getStockCount());

        verify(modelRepo).findByIsbn(isbn);
        verify(modelRepo, never()).update(any());
        verifyNoInteractions(unitRepo);
    }
}
