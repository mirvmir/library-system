package com.myapp.app.ui.console.menu.hook;

import com.myapp.app.controllers.console.action.GetBookRequestAction;
import com.myapp.app.controllers.requests.GetBookRequestRq;
import com.myapp.app.ui.console.UiSession;
import com.myapp.app.ui.console.requestBuilders.context.SortDirection;
import com.myapp.app.ui.console.requestBuilders.context.SortField;
import com.myapp.app.ui.console.requestBuilders.context.SortType;
import org.springframework.stereotype.Component;

@Component
public class BookRequestsBeforeRenderHook implements MenuHook {

    private final GetBookRequestAction getBookRequestAction;
    private final UiSession uiSession;

    public BookRequestsBeforeRenderHook(GetBookRequestAction action, UiSession uiSession) {
        this.getBookRequestAction = action;
        this.uiSession = uiSession;
    }

    @Override
    public void run() {
        if (!uiSession.getRequestSort()) {
            getBookRequestAction.execute(
                    new GetBookRequestRq(SortType.REQUEST,
                            SortDirection.ASC,
                            SortField.COUNT)
            );
        }
        uiSession.setRequestSort(false);
    }
}
