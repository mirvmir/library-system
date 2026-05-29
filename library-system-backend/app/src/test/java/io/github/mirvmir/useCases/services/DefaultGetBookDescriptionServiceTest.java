package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.BookNotFoundException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultGetBookDescriptionService;
import io.github.mirvmir.useCases.services.inputs.BookDescriptionInput;
import io.github.mirvmir.useCases.services.outputs.BookDescriptionOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DefaultGetBookDescriptionServiceTest {
    private BookModelRepository modelRepo;

    private DefaultGetBookDescriptionService service;

    @BeforeEach
    void setUp() {
        modelRepo = mock(BookModelRepository.class);

        service = new DefaultGetBookDescriptionService(modelRepo);
    }

    @Test
    void execute_shouldReturnBookDescription() {
        BookDescriptionInput input = new BookDescriptionInput("123");

        BookModel modelForGetDescription = new BookModel(
                input.isbn(),
                "Преступление и наказание",
                "Федор Михайлович Достоевский",
                new BigDecimal("345"),
                0,
                2
        );
        when(modelRepo.findByIsbn(input.isbn()))
                .thenReturn(modelForGetDescription);

        BookDescriptionOutput result = service.execute(new BookDescriptionInput(input.isbn()));

        verify(modelRepo).findByIsbn(input.isbn());

        assertEquals(modelForGetDescription.getIsbn(), result.isbn());
        assertEquals(modelForGetDescription.getAuthor(), result.author());
        assertEquals(modelForGetDescription.getTitle(), result.title());
        assertEquals(modelForGetDescription.getPrice(), result.price());
        assertEquals(modelForGetDescription.isAvailable(), result.available());
    }

    @Test
    void execute_shouldNotReturnBookDescription_shouldThrownException_whenBookNotFound() {
        BookDescriptionInput input = new BookDescriptionInput("123");
        when(modelRepo.findByIsbn(input.isbn()))
                .thenReturn(null);

        BookNotFoundException exception = assertThrows(
                BookNotFoundException.class,
                () -> service.execute(input)
        );
        assertEquals("Book with ISBN " + input.isbn() + " not found.", exception.getMessage());

        verify(modelRepo).findByIsbn(input.isbn());
    }
}
