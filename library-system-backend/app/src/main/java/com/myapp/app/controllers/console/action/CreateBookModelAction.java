package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.BookModelRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.CreateBookModelService;
import com.myapp.app.useCases.services.inputs.CreateBookModelInput;
import com.myapp.app.useCases.services.outputs.CreateBookModelOutput;
import org.springframework.stereotype.Component;

@Component
public class CreateBookModelAction implements IAction<BookModelRq> {

    private final CreateBookModelService service;
    private final IPresenter presenter;

    public CreateBookModelAction(CreateBookModelService service,
                                 IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(BookModelRq requestDto) {
        CreateBookModelInput request =
                new CreateBookModelInput(
                        requestDto.isbn(), requestDto.title(),
                        requestDto.author(), requestDto.price()
                );
        CreateBookModelOutput response = service.execute(request);
        presenter.present(response);
    }
}