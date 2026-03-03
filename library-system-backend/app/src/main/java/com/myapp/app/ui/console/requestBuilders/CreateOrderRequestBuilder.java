package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.OrderRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateOrderRequestBuilder implements RequestBuilder<OrderRq> {

    private final IView view;

    public CreateOrderRequestBuilder(IView view) {
        this.view = view;
    }

    @Override
    public OrderRq build(SystemContext context) {
        List<String> listIsbn = this.view.askForListIsbn();

        return new OrderRq(listIsbn, context.customerId());
    }
}
