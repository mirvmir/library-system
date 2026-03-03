package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.PeriodRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CalculateTotalEarningsByPeriodRequestBuilder
        implements RequestBuilder<PeriodRq> {

    private final IView view;

    public CalculateTotalEarningsByPeriodRequestBuilder(IView view) {
        this.view = view;
    }

    @Override
    public PeriodRq build(SystemContext context) {
        LocalDateTime from = this.view.askForStartDateTime("Введите дату начального периода: ");
        LocalDateTime to = this.view.askForEndDateTime("Введите дату конечного периода: ");

        return new PeriodRq(from, to);
    }
}
