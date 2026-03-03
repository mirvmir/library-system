package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.OrderIdRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.CompleteOrderService;
import com.myapp.app.useCases.services.inputs.CompleteOrderInput;
import com.myapp.app.useCases.services.outputs.CompleteOrderOutput;
import org.springframework.stereotype.Component;

@Component
public class CompleteOrderAction implements IAction<OrderIdRq> {

    private final CompleteOrderService service;
    private final IPresenter presenter;

    public CompleteOrderAction(CompleteOrderService service,
                               IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(OrderIdRq requestDto) {
        CompleteOrderInput request = new CompleteOrderInput(requestDto.orderId());
        CompleteOrderOutput response = service.execute(request);
        presenter.present(response);
    }
}

