package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.BookRequestRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

@Component
public class CreateBookRequestRequestBuilder implements RequestBuilder<BookRequestRq> {

    private final IView view;

    public CreateBookRequestRequestBuilder(IView view) {
        this.view = view;
    }

    @Override
    public BookRequestRq build(SystemContext context) {
        String isbn = view.askForString("Введите ISBN книги:");

        return new BookRequestRq(isbn, context.customerId());
    }
}
