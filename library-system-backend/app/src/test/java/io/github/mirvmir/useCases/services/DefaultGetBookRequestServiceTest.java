package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.IncompatibleSortTypesException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultGetBookRequestService;
import io.github.mirvmir.useCases.services.inputs.GetBookRequestInput;
import io.github.mirvmir.useCases.services.outputs.GetBookRequestsOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DefaultGetBookRequestServiceTest {
    private BookModelRepository modelRepo;

    private DefaultGetBookRequestService service;

    @BeforeEach
    void setUp() {
        modelRepo = mock(BookModelRepository.class);
        service = new DefaultGetBookRequestService(modelRepo);
    }

    @Test
    void execute_shouldReturnRequestedBooks_sortedByTitleAsc() {
        BookModel book1 = new BookModel("111", "Мастер и Маргарита", "Булгаков",
                new BigDecimal("500"), 0, 2);
        BookModel book2 = new BookModel("222", "Анна Каренина", "Толстой",
                new BigDecimal("400"), 3, 1);
        BookModel book3 = new BookModel("333", "Война и мир", "Толстой",
                new BigDecimal("700"), 2, 0);
        when(modelRepo.findAll()).thenReturn(List.of(book1, book2, book3));

        GetBookRequestInput input = new GetBookRequestInput("REQUEST", "ASC", "TITLE");

        GetBookRequestsOutput result = service.execute(input);

        assertEquals(2, result.bookRequests().size());
        assertEquals("Анна Каренина", result.bookRequests().get(0).title());
        assertEquals("Мастер и Маргарита", result.bookRequests().get(1).title());

        verify(modelRepo).findAll();
    }

    @Test
    void execute_shouldReturnRequestedBooks_sortedByTitleDesc() {
        BookModel book1 = new BookModel("111", "Мастер и Маргарита", "Булгаков",
                new BigDecimal("500"), 0, 2);
        BookModel book2 = new BookModel("222", "Анна Каренина", "Толстой",
                new BigDecimal("400"), 3, 1);
        BookModel book3 = new BookModel("333", "Война и мир", "Толстой",
                new BigDecimal("700"), 2, 0);
        when(modelRepo.findAll()).thenReturn(List.of(book1, book2, book3));

        GetBookRequestInput input = new GetBookRequestInput("REQUEST", "DESC", "TITLE");

        GetBookRequestsOutput result = service.execute(input);

        assertEquals(2, result.bookRequests().size());
        assertEquals("Мастер и Маргарита", result.bookRequests().get(0).title());
        assertEquals("Анна Каренина", result.bookRequests().get(1).title());

        verify(modelRepo).findAll();
    }

    @Test
    void execute_shouldReturnRequestedBooks_sortedByCountAsc() {
        BookModel book1 = new BookModel("111", "Мастер и Маргарита", "Булгаков",
                new BigDecimal("500"), 0, 5);
        BookModel book2 = new BookModel("222", "Анна Каренина", "Толстой",
                new BigDecimal("400"), 3, 1);
        BookModel book3 = new BookModel("333", "Война и мир", "Толстой",
                new BigDecimal("700"), 2, 0);
        BookModel book4 = new BookModel("444", "Идиот", "Достоевский",
                new BigDecimal("450"), 4, 3);
        when(modelRepo.findAll()).thenReturn(List.of(book1, book2, book3, book4));

        GetBookRequestInput input = new GetBookRequestInput("REQUEST", "ASC", "COUNT");

        GetBookRequestsOutput result = service.execute(input);

        assertEquals(3, result.bookRequests().size());
        assertEquals("Анна Каренина", result.bookRequests().get(0).title());
        assertEquals("Идиот", result.bookRequests().get(1).title());
        assertEquals("Мастер и Маргарита", result.bookRequests().get(2).title());

        verify(modelRepo).findAll();
    }

    @Test
    void execute_shouldReturnRequestedBooks_sortedByCountDesc() {
        BookModel book1 = new BookModel("111", "Мастер и Маргарита", "Булгаков",
                new BigDecimal("500"), 0, 5);
        BookModel book2 = new BookModel("222", "Анна Каренина", "Толстой",
                new BigDecimal("400"), 3, 1);
        BookModel book3 = new BookModel("333", "Война и мир", "Толстой",
                new BigDecimal("700"), 2, 0);
        BookModel book4 = new BookModel("444", "Идиот", "Достоевский",
                new BigDecimal("450"), 4, 3);
        when(modelRepo.findAll()).thenReturn(List.of(book1, book2, book3, book4));

        GetBookRequestInput input = new GetBookRequestInput("REQUEST", "DESC", "COUNT");

        GetBookRequestsOutput result = service.execute(input);

        assertEquals(3, result.bookRequests().size());
        assertEquals("Мастер и Маргарита", result.bookRequests().get(0).title());
        assertEquals("Идиот", result.bookRequests().get(1).title());
        assertEquals("Анна Каренина", result.bookRequests().get(2).title());

        verify(modelRepo).findAll();
    }

    // Накидала пару возможных случаев, хотя их и больше
    @ParameterizedTest
    @CsvSource({
            "REQUEST,ASC,PRICE",
            "REQUEST,DESC,AVAILABILITY",
            "REQUEST,DESC,DELIVERY_DATE",
            "REQUEST,DESC,COMPLETION_DATE",
            "REQUEST,DESC,STATUS",
            "ORDER,ASC,TITLE",
            "BOOK,ASC,TITLE",
            "STALE_BOOK,DESC,TITLE",
            "COMPLETED_ORDER,ASC,TITLE"
    })
    void execute_shouldNotReturnRequestedBooks_shouldThrowException_whenSortTypesAreIncompatible(
            String type,
            String field,
            String direction
    ) {
        GetBookRequestInput input = new GetBookRequestInput(type, direction, field);

        assertThrows(IncompatibleSortTypesException.class,
                () -> service.execute(input)
        );

        verify(modelRepo, never()).findAll();
    }
}
