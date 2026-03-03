package com.myapp.app.ui.console.menu.content;

import com.myapp.app.ui.console.UiSession;
import com.myapp.app.ui.console.consoleViewModels.consoleEntity.BookViewModel;
import com.myapp.app.ui.console.consoleViewModels.IView;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BooksMenuContent implements MenuContent {

    private final IView view;
    private final UiSession uiSession;

    public BooksMenuContent(IView view, UiSession uiSession) {
        this.view = view;
        this.uiSession = uiSession;
    }

    @Override
    public void render() {
        List<BookViewModel> books = uiSession.getBooks();
        view.displayItems(books);
    }
}
