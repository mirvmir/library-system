package io.github.mirvmir.controllers.web.requests;

import io.github.mirvmir.useCases.services.dto.SortDirection;
import io.github.mirvmir.useCases.services.dto.SortField;
import io.github.mirvmir.useCases.services.dto.SortType;

import java.time.LocalDateTime;

public record GetOrderRq(SortType type,
                         Boolean filtered,
                         LocalDateTime from,
                         LocalDateTime to,
                         SortDirection direction,
                         SortField field) {
    public GetOrderRq {
        if (null == type) {
            type = SortType.ORDER;
        }
        if (null == direction) {
            direction = SortDirection.ASC;
        }
        if (null == field) {
            field = SortField.PRICE;
        }
        if (null == filtered) {
            filtered = false;
        }
    }
}
