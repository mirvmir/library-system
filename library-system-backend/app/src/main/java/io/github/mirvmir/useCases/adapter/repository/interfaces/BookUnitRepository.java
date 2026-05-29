package io.github.mirvmir.useCases.adapter.repository.interfaces;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.bookUnit.BookUnit;
import io.github.mirvmir.useCases.services.dto.StaleUnitRow;

import java.time.LocalDate;
import java.util.List;

public interface BookUnitRepository {
    BookUnit save(BookUnit bookUnit);
    BookUnit update(BookUnit bookUnit);
    BookUnit findByIsbn(BookModel bookModel);
    List<BookUnit> findAll();
    List<StaleUnitRow> findStaleUnitsByDeliveryDate(LocalDate staleDate, String direction);
    List<StaleUnitRow> findStaleUnitsByPrice(LocalDate staleDate, String direction);
    BookUnit findByIsbnForUpdate(String isbn);
    void markSold(List<Long> bookUnitIds);
    void releaseReservedUnits(List<Long> reservedBookUnitIds);
}
