package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.EmptyRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.CreateCustomerService;
import com.myapp.app.useCases.services.inputs.CreateCustomerInput;
import com.myapp.app.useCases.services.outputs.CreateCustomerOutput;
import org.springframework.stereotype.Component;

@Component
public class CreateCustomerAction implements IAction<EmptyRq> {

    private final CreateCustomerService service;
    private final IPresenter presenter;

    public CreateCustomerAction(CreateCustomerService service, IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(EmptyRq requestDto) {
        CreateCustomerInput request = new CreateCustomerInput();
        CreateCustomerOutput response = service.execute(request);

        presenter.present(response);
    }
}
