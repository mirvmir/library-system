package com.myapp.app.ui.console.menu.content;

import com.myapp.app.ui.console.UiSession;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.consoleViewModels.consoleEntity.OrderViewModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrdersMenuContent implements MenuContent {

    private final IView view;
    private final UiSession uiSession;

    public OrdersMenuContent(IView view, UiSession uiSession) {
        this.view = view;
        this.uiSession = uiSession;
    }

    @Override
    public void render() {
        List<OrderViewModel> orders = uiSession.getOrders();
        view.displayItems(orders);
    }
}
