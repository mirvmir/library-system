package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.OrderIdRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.CancelOrderService;
import com.myapp.app.useCases.services.inputs.CancelOrderInput;
import com.myapp.app.useCases.services.outputs.CancelOrderOutput;
import org.springframework.stereotype.Component;

@Component
public class CancelOrderAction implements IAction<OrderIdRq> {

    private final CancelOrderService service;
    private final IPresenter presenter;

    public CancelOrderAction(CancelOrderService service, IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(OrderIdRq requestDto) {
        CancelOrderInput request = new CancelOrderInput(requestDto.orderId());
        CancelOrderOutput response = service.execute(request);
        presenter.present(response);
    }
}
