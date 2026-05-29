package io.github.mirvmir.controllers.web.requests;

import io.github.mirvmir.useCases.services.dto.SortDirection;
import io.github.mirvmir.useCases.services.dto.SortField;
import io.github.mirvmir.useCases.services.dto.SortType;

public record GetBookRq(SortType type,
                        SortDirection direction,
                        SortField field) {
    public GetBookRq {
        if (type == null) {
            type = SortType.BOOK;
        }
        if (direction == null) {
            direction = SortDirection.ASC;
        }
        if (field == null) {
            field = SortField.TITLE;
        }
    }
}
