package com.myapp.app.ui.console.menu.content;

import com.myapp.app.ui.console.UiSession;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.consoleViewModels.consoleEntity.BookRequestViewModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookRequestsMenuContent implements MenuContent {

    private final IView view;
    private final UiSession uiSession;

    public BookRequestsMenuContent(IView view, UiSession uiSession) {
        this.view = view;
        this.uiSession = uiSession;
    }

    @Override
    public void render() {
        List<BookRequestViewModel> requests = uiSession.getRequests();
        view.displayItems(requests);
    }
}
