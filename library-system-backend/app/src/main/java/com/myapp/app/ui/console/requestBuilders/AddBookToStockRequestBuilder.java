package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.IsbnRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

@Component
public class AddBookToStockRequestBuilder implements RequestBuilder<IsbnRq> {

    private final IView view;

    public AddBookToStockRequestBuilder(IView view) {
        this.view = view;
    }

    @Override
    public IsbnRq build(SystemContext context) {
        String isbn = view.askForString("Введите ISBN:");

        return new IsbnRq(isbn);
    }
}
