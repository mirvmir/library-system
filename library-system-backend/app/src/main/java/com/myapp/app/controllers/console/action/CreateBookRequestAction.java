package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.BookRequestRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.CreateBookRequestService;
import com.myapp.app.useCases.services.inputs.CreateBookRequestInput;
import com.myapp.app.useCases.services.outputs.CreateBookRequestOutput;
import org.springframework.stereotype.Component;

@Component
public class CreateBookRequestAction implements IAction<BookRequestRq> {

    private final CreateBookRequestService service;
    private final IPresenter presenter;

    public CreateBookRequestAction(CreateBookRequestService service, IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(BookRequestRq requestDto) {
        CreateBookRequestInput request = new CreateBookRequestInput(requestDto.isbn(), requestDto.customerId());
        CreateBookRequestOutput response = service.execute(request);
        presenter.present(response);
    }
}
