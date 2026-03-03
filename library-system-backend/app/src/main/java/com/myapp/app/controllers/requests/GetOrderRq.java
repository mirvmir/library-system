package com.myapp.app.controllers.requests;

import com.myapp.app.ui.console.requestBuilders.context.SortDirection;
import com.myapp.app.ui.console.requestBuilders.context.SortField;
import com.myapp.app.ui.console.requestBuilders.context.SortType;

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
