package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.IncompatibleSortTypesException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookUnitRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultGetBookService;
import io.github.mirvmir.useCases.services.inputs.GetBookInput;
import io.github.mirvmir.useCases.services.outputs.GetBooksOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DefaultGetBookServiceTest {
    private BookModelRepository modelRepo;
    private BookUnitRepository unitRepo;
    private Config config;

    private DefaultGetBookService service;

    @BeforeEach
    void setUp() {
        modelRepo = mock(BookModelRepository.class);
        unitRepo = mock(BookUnitRepository.class);
        config = mock(Config.class);

        service = new DefaultGetBookService(modelRepo, unitRepo, config);
    }

    @Test
    void execute_shouldReturnBooks_sortedByTitleAsc() {
        BookModel book1 = new BookModel("111", "Мастер и Маргарита", "Булгаков",
                new BigDecimal("500"), 0, 2);
        BookModel book2 = new BookModel("222", "Анна Каренина", "Толстой",
                new BigDecimal("400"), 3, 1);
        BookModel book3 = new BookModel("333", "Война и мир", "Толстой",
                new BigDecimal("700"), 2, 0);
        when(modelRepo.findAll()).thenReturn(List.of(book1, book2, book3));

        GetBookInput input = new GetBookInput("BOOK", "ASC", "TITLE");

        GetBooksOutput result = service.execute(input);

        assertEquals(3, result.books().size());
        assertEquals("Анна Каренина", result.books().get(0).title());
        assertEquals("Война и мир", result.books().get(1).title());
        assertEquals("Мастер и Маргарита", result.books().get(2).title());

        verify(modelRepo).findAll();
    }

    @Test
    void execute_shouldReturnBooks_sortedByTitleDesc() {
        BookModel book1 = new BookModel("111", "Мастер и Маргарита", "Булгаков",
                new BigDecimal("500"), 0, 2);
        BookModel book2 = new BookModel("222", "Анна Каренина", "Толстой",
                new BigDecimal("400"), 3, 1);
        BookModel book3 = new BookModel("333", "Война и мир", "Толстой",
                new BigDecimal("700"), 2, 0);
        when(modelRepo.findAll()).thenReturn(List.of(book1, book2, book3));

        GetBookInput input = new GetBookInput("BOOK", "DESC", "TITLE");

        GetBooksOutput result = service.execute(input);

        assertEquals(3, result.books().size());
        assertEquals("Мастер и Маргарита", result.books().get(0).title());
        assertEquals("Война и мир", result.books().get(1).title());
        assertEquals("Анна Каренина", result.books().get(2).title());

        verify(modelRepo).findAll();
    }

    @Test
    void execute_shouldReturnBooks_sortedByPiceAsc() {
        BookModel book1 = new BookModel("111", "Мастер и Маргарита", "Булгаков",
                new BigDecimal("500"), 0, 5);
        BookModel book2 = new BookModel("222", "Анна Каренина", "Толстой",
                new BigDecimal("400"), 3, 1);
        BookModel book3 = new BookModel("333", "Война и мир", "Толстой",
                new BigDecimal("700"), 2, 0);
        when(modelRepo.findAll()).thenReturn(List.of(book1, book2, book3));

        GetBookInput input = new GetBookInput("BOOK", "ASC", "PRICE");

        GetBooksOutput result = service.execute(input);

        assertEquals(3, result.books().size());
        assertEquals("Анна Каренина", result.books().get(0).title());
        assertEquals("Мастер и Маргарита", result.books().get(1).title());
        assertEquals("Война и мир", result.books().get(2).title());

        verify(modelRepo).findAll();
    }

    @Test
    void execute_shouldReturnBooks_sortedByPriceDesc() {
        BookModel book1 = new BookModel("111", "Мастер и Маргарита", "Булгаков",
                new BigDecimal("500"), 0, 5);
        BookModel book2 = new BookModel("222", "Анна Каренина", "Толстой",
                new BigDecimal("400"), 3, 1);
        BookModel book3 = new BookModel("333", "Война и мир", "Толстой",
                new BigDecimal("700"), 2, 0);
        when(modelRepo.findAll()).thenReturn(List.of(book1, book2, book3));

        GetBookInput input = new GetBookInput("BOOK", "DESC", "PRICE");

        GetBooksOutput result = service.execute(input);

        assertEquals(3, result.books().size());
        assertEquals("Война и мир", result.books().get(0).title());
        assertEquals("Мастер и Маргарита", result.books().get(1).title());
        assertEquals("Анна Каренина", result.books().get(2).title());

        verify(modelRepo).findAll();
    }

    @Test
    void execute_shouldReturnBooks_sortedByAvailabilityAsc() {
        BookModel book1 = new BookModel("111", "Мастер и Маргарита", "Булгаков",
                new BigDecimal("500"), 0, 5);
        BookModel book2 = new BookModel("222", "Анна Каренина", "Толстой",
                new BigDecimal("400"), 3, 1);
        BookModel book3 = new BookModel("333", "Война и мир", "Толстой",
                new BigDecimal("700"), 2, 0);
        when(modelRepo.findAll()).thenReturn(List.of(book1, book2, book3));

        GetBookInput input = new GetBookInput("BOOK", "ASC", "AVAILABILITY");

        GetBooksOutput result = service.execute(input);

        assertEquals(3, result.books().size());
        assertEquals("Мастер и Маргарита", result.books().get(0).title());
        assertEquals("Анна Каренина", result.books().get(1).title());
        assertEquals("Война и мир", result.books().get(2).title());

        verify(modelRepo).findAll();
    }

    @Test
    void execute_shouldReturnBooks_sortedByAvailabilityDesc() {
        BookModel book1 = new BookModel("111", "Мастер и Маргарита", "Булгаков",
                new BigDecimal("500"), 0, 5);
        BookModel book2 = new BookModel("222", "Анна Каренина", "Толстой",
                new BigDecimal("400"), 3, 1);
        BookModel book3 = new BookModel("333", "Война и мир", "Толстой",
                new BigDecimal("700"), 2, 0);
        when(modelRepo.findAll()).thenReturn(List.of(book1, book2, book3));

        GetBookInput input = new GetBookInput("BOOK", "DESC", "AVAILABILITY");

        GetBooksOutput result = service.execute(input);

        assertEquals(3, result.books().size());
        assertEquals("Анна Каренина", result.books().get(0).title());
        assertEquals("Война и мир", result.books().get(1).title());
        assertEquals("Мастер и Маргарита", result.books().get(2).title());

        verify(modelRepo).findAll();
    }

    @Test
    void execute_shouldReturnStaledBooks_sortedByDeliveryDateAsc() {
        GetBookInput input = new GetBookInput("STALE_BOOK", "ASC", "DELIVERY_DATE");

        int months = 6;
        when(config.getStaleBookMonths())
                .thenReturn(months);

        LocalDate expectedDate = LocalDate.now().minusMonths(months);

        when(unitRepo.findStaleUnitsByDeliveryDate(any(), eq("ASC")))
                .thenReturn(List.of());

        service.execute(input);

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(unitRepo).findStaleUnitsByDeliveryDate(dateCaptor.capture(), eq("ASC"));

        assertEquals(expectedDate, dateCaptor.getValue());
    }

    @Test
    void execute_shouldReturnStaledBooks_sortedByDeliveryDateDesc() {
        GetBookInput input = new GetBookInput("STALE_BOOK", "DESC", "DELIVERY_DATE");

        int months = 6;
        when(config.getStaleBookMonths())
                .thenReturn(months);

        LocalDate expectedDate = LocalDate.now().minusMonths(months);

        when(unitRepo.findStaleUnitsByDeliveryDate(any(), eq("DESC")))
                .thenReturn(List.of());

        service.execute(input);

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(unitRepo).findStaleUnitsByDeliveryDate(dateCaptor.capture(), eq("DESC"));

        assertEquals(expectedDate, dateCaptor.getValue());
    }

    @Test
    void execute_shouldReturnStaledBooks_sortedByPriceAsc() {
        GetBookInput input = new GetBookInput("STALE_BOOK", "ASC", "PRICE");

        int months = 6;
        when(config.getStaleBookMonths())
                .thenReturn(months);

        LocalDate expectedDate = LocalDate.now().minusMonths(months);

        when(unitRepo.findStaleUnitsByPrice(any(), eq("ASC")))
                .thenReturn(List.of());

        service.execute(input);

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(unitRepo).findStaleUnitsByPrice(dateCaptor.capture(), eq("ASC"));

        assertEquals(expectedDate, dateCaptor.getValue());
    }

    @Test
    void execute_shouldReturnStaledBooks_sortedByPriceDesc() {
        GetBookInput input = new GetBookInput("STALE_BOOK", "DESC", "PRICE");

        int months = 6;
        when(config.getStaleBookMonths())
                .thenReturn(months);

        LocalDate expectedDate = LocalDate.now().minusMonths(months);

        when(unitRepo.findStaleUnitsByPrice(any(), eq("DESC")))
                .thenReturn(List.of());

        service.execute(input);

        ArgumentCaptor<LocalDate> dateCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(unitRepo).findStaleUnitsByPrice(dateCaptor.capture(), eq("DESC"));

        assertEquals(expectedDate, dateCaptor.getValue());
    }

    // Накидала пару возможных случаев, хотя их и больше
    @ParameterizedTest
    @CsvSource({
            "REQUEST,PRICE,ASC",
            "REQUEST,AUTHOR,DESC",
            "ORDER,TITLE,ASC",
            "BOOK,DELIVERY_DATE,ASC",
            "STALE_BOOK,TITLE,DESC",
            "COMPLETED_ORDER,TITLE,ASC"
    })
    void execute_shouldNotReturnRequestedBooks_shouldThrowException_whenSortTypesAreIncompatible(
            String type,
            String field,
            String direction) {
        GetBookInput input = new GetBookInput(type, direction, field);

        assertThrows(IncompatibleSortTypesException.class,
                () -> service.execute(input)
        );

        verify(modelRepo, never()).findAll();
    }
}
