package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.GetBookRequestRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.GetBookRequestService;
import com.myapp.app.useCases.services.inputs.GetBookRequestInput;
import com.myapp.app.useCases.services.outputs.GetBookRequestsOutput;
import org.springframework.stereotype.Component;

@Component
public class GetBookRequestAction implements IAction<GetBookRequestRq> {
    private final GetBookRequestService service;
    private final IPresenter presenter;

    public GetBookRequestAction(GetBookRequestService service,
                                IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(GetBookRequestRq requestDto) {
        GetBookRequestInput request = new GetBookRequestInput(
                requestDto.type().toString(),
                requestDto.direction().toString(),
                requestDto.field().toString()
        );
        GetBookRequestsOutput response = service.execute(request);

        presenter.present(response);
    }
}
