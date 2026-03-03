package com.myapp.app.useCases.adapter.repository.interfaces;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.domain.entities.bookUnit.BookUnit;
import com.myapp.app.useCases.services.dto.StaleUnitRow;

import java.time.LocalDate;
import java.util.List;

public interface BookUnitRepository {
    BookUnit save(BookUnit bookUnit);
    BookUnit update(BookUnit bookUnit);
    BookUnit findByIsbn(BookModel bookModel);
    List<BookUnit> findAll();
    List<StaleUnitRow> findStaleUnitsByDeliveryDate(LocalDate staleDate, String direction);
    List<StaleUnitRow> findStaleUnitsByPrice(LocalDate staleDate, String direction);
    List<Long> reserveBookUnitId(List<String> isbns);
}
