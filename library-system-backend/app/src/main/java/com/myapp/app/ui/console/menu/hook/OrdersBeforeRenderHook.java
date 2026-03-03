package com.myapp.app.ui.console.menu.hook;

import com.myapp.app.controllers.console.action.GetOrderAction;
import com.myapp.app.controllers.requests.GetOrderRq;
import com.myapp.app.ui.console.UiSession;
import com.myapp.app.ui.console.requestBuilders.context.SortDirection;
import com.myapp.app.ui.console.requestBuilders.context.SortField;
import com.myapp.app.ui.console.requestBuilders.context.SortType;
import org.springframework.stereotype.Component;

@Component
public class OrdersBeforeRenderHook implements MenuHook {

    private final GetOrderAction getOrderAction;
    private final UiSession uiSession;

    public OrdersBeforeRenderHook(GetOrderAction action, UiSession uiSession) {
        this.getOrderAction = action;
        this.uiSession = uiSession;
    }

    @Override
    public void run() {
        if (!uiSession.getOrderSort()) {
            getOrderAction.execute(
                    new GetOrderRq(SortType.ORDER,
                            false,
                            null,
                            null,
                            SortDirection.ASC,
                            SortField.PRICE)
            );
        }
        uiSession.setOrderSort(false);
    }
}
