package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.PathRq;
import com.myapp.app.exception.AppException;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.ImportOrderCsvService;
import com.myapp.app.useCases.services.inputs.ImportOrderCsvInput;
import com.myapp.app.useCases.services.outputs.ImportOrderOutput;
import org.springframework.stereotype.Component;

@Component
public class ImportOrderCsvAction implements IAction<PathRq> {

    private final ImportOrderCsvService service;
    private final IPresenter presenter;

    public ImportOrderCsvAction(ImportOrderCsvService service, IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(PathRq request) throws AppException {
        ImportOrderCsvInput params = new ImportOrderCsvInput(request.path());
        ImportOrderOutput response = service.execute(params);

        presenter.present(response);
    }
}