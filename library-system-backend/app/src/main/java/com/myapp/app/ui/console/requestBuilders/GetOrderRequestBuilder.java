package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.GetOrderRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SortDirection;
import com.myapp.app.ui.console.requestBuilders.context.SortType;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GetOrderRequestBuilder implements RequestBuilder<GetOrderRq>  {

    private final IView view;

    public GetOrderRequestBuilder(IView view) {
        this.view = view;
    }

    @Override
    public GetOrderRq build(SystemContext context) {

        LocalDateTime from = null;
        LocalDateTime to = null;

        boolean filtered = context.sortType() == SortType.COMPLETED_ORDER;
        if (filtered) {
            from = this.view.askForStartDateTime("Введите дату начального периода ");
            to = this.view.askForEndDateTime("Введите дату конечного периода ");
        }

        SortDirection direction = SortDirection.valueOf(
                this.view.askForString("Направление данных (ASC or DESC): ")
        );

        return new GetOrderRq(context.sortType(),
                filtered,
                from,
                to,
                direction,
                context.sortField());
    }
}
