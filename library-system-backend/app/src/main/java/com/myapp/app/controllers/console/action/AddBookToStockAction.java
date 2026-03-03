package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.IsbnRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.AddBookToStockService;
import com.myapp.app.useCases.services.inputs.AddBookToStockInput;
import com.myapp.app.useCases.services.outputs.AddBookToStockOutput;
import org.springframework.stereotype.Component;

@Component
public class AddBookToStockAction implements IAction<IsbnRq> {
    private final AddBookToStockService service;
    private final IPresenter presenter;

    public AddBookToStockAction(AddBookToStockService service,
                                IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(IsbnRq requestDto) {
        AddBookToStockInput request = new AddBookToStockInput(requestDto.isbn());
        AddBookToStockOutput response = service.execute(request);

        presenter.present(response);
    }
}