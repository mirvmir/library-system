package io.github.mirvmir.controllers.web.requests;

import io.github.mirvmir.useCases.services.dto.SortDirection;
import io.github.mirvmir.useCases.services.dto.SortField;
import io.github.mirvmir.useCases.services.dto.SortType;

public record GetBookRequestRq(SortType type,
                               SortDirection direction,
                               SortField field) {
    public GetBookRequestRq {
        if (type == null) {
            type = SortType.REQUEST;
        }
        if (direction == null) {
            direction = SortDirection.ASC;
        }
        if (field == null) {
            field = SortField.TITLE;
        }
    }
}
