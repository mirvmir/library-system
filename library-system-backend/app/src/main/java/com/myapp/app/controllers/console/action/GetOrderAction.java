package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.GetOrderRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.GetOrderService;
import com.myapp.app.useCases.services.inputs.GetOrderInput;
import com.myapp.app.useCases.services.outputs.GetOrdersOutput;
import org.springframework.stereotype.Component;

@Component
public class GetOrderAction implements IAction<GetOrderRq> {
    private final GetOrderService service;
    private final IPresenter presenter;

    public GetOrderAction(GetOrderService service,
                         IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(GetOrderRq requestDto) {
        GetOrderInput request = new GetOrderInput(
                requestDto.type().toString(),
                requestDto.filtered(),
                requestDto.direction().toString(),
                requestDto.field().toString(),
                requestDto.from(),
                requestDto.to()
        );
        GetOrdersOutput response = service.execute(request);

        presenter.present(response);
    }
}
