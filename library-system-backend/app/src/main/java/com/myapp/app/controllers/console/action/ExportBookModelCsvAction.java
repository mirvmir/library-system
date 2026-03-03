package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.PathRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.ExportBookModelCsvService;
import com.myapp.app.useCases.services.inputs.ExportBookModelCsvInput;
import com.myapp.app.useCases.services.outputs.ExportBookModelOutput;
import org.springframework.stereotype.Component;

@Component
public class ExportBookModelCsvAction implements IAction<PathRq> {

    private final ExportBookModelCsvService service;
    private final IPresenter presenter;

    public ExportBookModelCsvAction(ExportBookModelCsvService service,
                                    IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(PathRq requestDto) {
        ExportBookModelCsvInput request = new ExportBookModelCsvInput(requestDto.path());
        ExportBookModelOutput response = service.execute(request);
        presenter.present(response);
    }
}
