package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.DuplicateIsbnException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultCreateBookModelService;
import io.github.mirvmir.useCases.services.inputs.CreateBookModelInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DefaultCreateBookModelServiceTest {
    private BookModelRepository modelRepo;

    private DefaultCreateBookModelService service;

    @BeforeEach
    void setUp() {
        modelRepo = mock(BookModelRepository.class);

        service = new DefaultCreateBookModelService(modelRepo);
    }

    @Test
    void execute_shouldCreateBookModel() {
        CreateBookModelInput input = new CreateBookModelInput(
                "123",
                "Преступление и наказание",
                "Федор Михайлович Достоевский",
                new BigDecimal("345")
        );

        when(modelRepo.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));

        service.execute(input);

        ArgumentCaptor<BookModel> captor = ArgumentCaptor.forClass(BookModel.class);
        verify(modelRepo).save(captor.capture());
        BookModel bookForSave = captor.getValue();
        assertEquals(input.isbn(), bookForSave.getIsbn());
        assertEquals(input.title(), bookForSave.getTitle());
        assertEquals(input.author(), bookForSave.getAuthor());
        assertEquals(input.price(), bookForSave.getPrice());
    }

    @Test
    void execute_shouldNotCreateBookModel_shouldThrowException_whenRepositoryThrowsException() {
        CreateBookModelInput input = new CreateBookModelInput(
                "123",
                "Преступление и наказание",
                "Федор Михайлович Достоевский",
                new BigDecimal("345")
        );

        when(modelRepo.save(any(BookModel.class)))
                .thenThrow(new RuntimeException("duplicate key"));

        DuplicateIsbnException ex = assertThrows(
                DuplicateIsbnException.class,
                () -> service.execute(input)
        );
        assertEquals("Book with ISBN " + input.isbn() + " already exists.", ex.getMessage());
    }
}
