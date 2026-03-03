package com.myapp.app.ui.console.menu.hook;

import com.myapp.app.controllers.console.action.GetBookAction;
import com.myapp.app.controllers.requests.GetBookRq;
import com.myapp.app.ui.console.UiSession;
import com.myapp.app.ui.console.requestBuilders.context.SortDirection;
import com.myapp.app.ui.console.requestBuilders.context.SortField;
import com.myapp.app.ui.console.requestBuilders.context.SortType;
import org.springframework.stereotype.Component;

@Component
public class BooksBeforeRenderHook implements MenuHook {

    private final GetBookAction getBookAction;
    private final UiSession uiSession;

    public BooksBeforeRenderHook(GetBookAction action, UiSession uiSession) {
        this.getBookAction = action;
        this.uiSession = uiSession;
    }

    @Override
    public void run() {
        if (!uiSession.getBookSort()) {
            getBookAction.execute(
                    new GetBookRq(SortType.BOOK,
                            SortDirection.ASC,
                            SortField.TITLE)
            );
        }
        uiSession.setBookSort(false);
    }
}
