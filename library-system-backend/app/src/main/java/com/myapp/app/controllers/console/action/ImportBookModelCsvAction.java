package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.PathRq;
import com.myapp.app.exception.AppException;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.ImportBookModelCsvService;
import com.myapp.app.useCases.services.inputs.ImportBookModelCsvInput;
import com.myapp.app.useCases.services.outputs.ImportBookModelOutput;
import org.springframework.stereotype.Component;

@Component
public class ImportBookModelCsvAction implements IAction<PathRq> {

    private final ImportBookModelCsvService service;
    private final IPresenter presenter;

    public ImportBookModelCsvAction(ImportBookModelCsvService service,
                                    IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(PathRq request) throws AppException {
        ImportBookModelCsvInput params = new ImportBookModelCsvInput(request.path());
        ImportBookModelOutput response = service.execute(params);

        presenter.present(response);
    }
}