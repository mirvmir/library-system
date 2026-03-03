package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.GetBookRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.GetBookService;
import com.myapp.app.useCases.services.inputs.GetBookInput;
import com.myapp.app.useCases.services.outputs.GetBooksOutput;
import org.springframework.stereotype.Component;

@Component
public class GetBookAction implements IAction<GetBookRq> {

    private final GetBookService service;
    private final IPresenter presenter;

    public GetBookAction(GetBookService service,
                         IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(GetBookRq requestDto) {
        GetBookInput request = new GetBookInput(
                requestDto.type().toString(),
                requestDto.direction().toString(),
                requestDto.field().toString()
        );
        GetBooksOutput response = service.execute(request);

        presenter.present(response);
    }
}
