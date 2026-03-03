package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.IsbnRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

@Component
public class WriteOffBookRqBuilder implements RequestBuilder<IsbnRq> {

    private final IView view;

    public WriteOffBookRqBuilder(IView view) {
        this.view = view;
    }

    @Override
    public IsbnRq build(SystemContext context) {
        String isbn = this.view.askForString("Введите ISBN книги для списания: ");

        return new IsbnRq(isbn);
    }
}
