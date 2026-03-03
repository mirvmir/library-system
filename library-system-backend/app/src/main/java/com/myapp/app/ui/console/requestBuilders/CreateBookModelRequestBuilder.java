package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.BookModelRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreateBookModelRequestBuilder implements RequestBuilder<BookModelRq> {

    private final IView view;

    public CreateBookModelRequestBuilder(IView view) {
        this.view = view;
    }

    @Override
    public BookModelRq build(SystemContext context) {
        String isbn = view.askForString("Введите ISBN:");
        String title = view.askForString("Введите название:");
        String author = view.askForString("Введите автора:");
        BigDecimal price = view.askForBigDecimal("Введите цену:");

        return new BookModelRq(isbn, title, author, price);
    }
}
