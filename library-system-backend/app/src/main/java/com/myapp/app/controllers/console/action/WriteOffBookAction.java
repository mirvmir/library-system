package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.IsbnRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.WriteOffBookService;
import com.myapp.app.useCases.services.inputs.WriteOffBookInput;
import com.myapp.app.useCases.services.outputs.WriteOffBookOutput;
import org.springframework.stereotype.Component;

@Component
public class WriteOffBookAction implements IAction<IsbnRq> {

    private final WriteOffBookService service;
    private final IPresenter presenter;

    public WriteOffBookAction(WriteOffBookService service,
                              IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(IsbnRq request) {
        WriteOffBookInput requestDto = new WriteOffBookInput(request.isbn());
        WriteOffBookOutput response = service.execute(requestDto);

        presenter.present(response);
    }
}