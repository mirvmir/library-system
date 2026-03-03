package com.myapp.app.controllers.console.action;

import com.myapp.app.controllers.console.IAction;
import com.myapp.app.controllers.requests.PeriodRq;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.interfaces.CalculateTotalEarningsByPeriodService;
import com.myapp.app.useCases.services.inputs.CalculateTotalEarningsByPeriodInput;
import com.myapp.app.useCases.services.outputs.CalculateTotalEarningsByPeriodOutput;
import org.springframework.stereotype.Component;

@Component
public class CalculateTotalEarningsByPeriodAction implements IAction<PeriodRq> {

    private final CalculateTotalEarningsByPeriodService service;
    private final IPresenter presenter;

    public CalculateTotalEarningsByPeriodAction(CalculateTotalEarningsByPeriodService service, IPresenter presenter) {
        this.service = service;
        this.presenter = presenter;
    }

    @Override
    public void execute(PeriodRq requestDto) {
        CalculateTotalEarningsByPeriodInput request =
                new CalculateTotalEarningsByPeriodInput(requestDto.from(), requestDto.to());
        CalculateTotalEarningsByPeriodOutput response = service.execute(request);

        presenter.present(response);
    }
}
