package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.PathRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.ExportOrderCsvService;
import com.myapp.app.useCases.services.inputs.ExportOrderCsvInput;
import com.myapp.app.useCases.services.outputs.ExportOrderOutput;
import org.springframework.stereotype.Component;

@Component
public class ExportOrderCsvAction implements IAction<PathRq> {

    private final ExportOrderCsvService service;
    private final IPresenter presenter;

    public ExportOrderCsvAction(ExportOrderCsvService service,
                                IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(PathRq requestDto) {
        ExportOrderCsvInput params = new ExportOrderCsvInput(requestDto.path());
        ExportOrderOutput response = service.execute(params);

        presenter.present(response);
    }
}
