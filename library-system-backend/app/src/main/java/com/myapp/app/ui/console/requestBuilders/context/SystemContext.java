package com.myapp.app.ui.console.requestBuilders.context;

public record SystemContext(Long customerId,
                            SortField sortField,
                            SortType sortType) {
}