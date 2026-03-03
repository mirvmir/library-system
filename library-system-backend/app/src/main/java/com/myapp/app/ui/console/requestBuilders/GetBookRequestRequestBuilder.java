package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.GetBookRequestRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SortDirection;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

@Component
public class GetBookRequestRequestBuilder implements RequestBuilder<GetBookRequestRq>  {

    private final IView view;

    public GetBookRequestRequestBuilder(IView view) {
        this.view = view;
    }

    @Override
    public GetBookRequestRq build(SystemContext context) {
        SortDirection direction = SortDirection.valueOf(
                this.view.askForString("Направление данных (ASC or DESC): ")
        );

        return new GetBookRequestRq(context.sortType(),
                direction,
                context.sortField());
    }
}
