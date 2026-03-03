package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.GetBookRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SortDirection;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

@Component
public class GetBookRequestBuilder implements RequestBuilder<GetBookRq> {

    private final IView view;

    public GetBookRequestBuilder(IView view) {
        this.view = view;
    }

    @Override
    public GetBookRq build(SystemContext context) {

        SortDirection direction = SortDirection.valueOf(
                this.view.askForString("Направление данных (ASC or DESC): ")
        );

        return new GetBookRq(context.sortType(),
                direction,
                context.sortField());
    }
}
