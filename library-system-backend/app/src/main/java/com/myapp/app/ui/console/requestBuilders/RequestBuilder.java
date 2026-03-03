package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.ui.console.requestBuilders.context.SystemContext;

public interface RequestBuilder<T> {
    T build(SystemContext context);
}