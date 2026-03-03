package com.myapp.app.controllers.requests;

import com.myapp.app.ui.console.requestBuilders.context.SortDirection;
import com.myapp.app.ui.console.requestBuilders.context.SortField;
import com.myapp.app.ui.console.requestBuilders.context.SortType;

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
