package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.OrderIdRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

@Component
public class CancelOrderRequestBuilder implements RequestBuilder<OrderIdRq> {

    private final IView view;

    public CancelOrderRequestBuilder(IView view) {
        this.view = view;
    }

    @Override
    public OrderIdRq build(SystemContext context) {
        Long orderId = view.askForLong("Введите номер заказа:");

        return new OrderIdRq(orderId);
    }
}
