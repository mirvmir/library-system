package io.github.mirvmir.useCases.services.mapping;

import io.github.mirvmir.useCases.services.dto.StaleUnitRow;
import io.github.mirvmir.useCases.services.outputs.GetBookOutput;

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
