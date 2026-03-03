package com.myapp.app.useCases.services.mapping;

import com.myapp.app.useCases.services.dto.StaleUnitRow;
import com.myapp.app.useCases.services.outputs.GetBookOutput;

public class StaleUnitRowToGetBookRsMapper {
    public static GetBookOutput map(StaleUnitRow book) {
        return new GetBookOutput(
                book.isbn(),
                book.title(),
                book.author(),
                book.price().toString(),
                true
        );
    }
}
