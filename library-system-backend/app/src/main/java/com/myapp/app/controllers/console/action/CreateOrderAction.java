package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.OrderRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.CreateOrderService;
import com.myapp.app.useCases.services.inputs.CreateOrderInput;
import com.myapp.app.useCases.services.outputs.CreateOrderOutput;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderAction implements IAction<OrderRq> {

    private final CreateOrderService service;
    private final IPresenter presenter;

    public CreateOrderAction(CreateOrderService service,
                             IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(OrderRq requestDto) {
        CreateOrderInput request = new CreateOrderInput(requestDto.customerId(), requestDto.listIsbn());
        CreateOrderOutput response = service.execute(request);
        presenter.present(response);
    }
}
