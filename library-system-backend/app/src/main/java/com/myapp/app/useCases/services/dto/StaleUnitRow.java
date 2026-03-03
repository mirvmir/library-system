package com.myapp.app.useCases.services.dto;

import com.myapp.app.domain.entities.bookUnit.BookUnit;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StaleUnitRow(Long unitId, String isbn, LocalDate deliveryDate, BigDecimal price, String title, String author) {
    public BookUnit toUnit() {
        BookUnit unit = new BookUnit(isbn, true, deliveryDate);
        unit.setId(unitId);
        return unit;
    }
}
