package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.frameworks.Config;
import com.myapp.app.controllers.requests.PathRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

@Component
public class ImportOrderCsvRequestBuilder implements RequestBuilder<PathRq> {

    private final Config config;
    private final IView view;

    public ImportOrderCsvRequestBuilder(Config config, IView view) {
        this.config = config;
        this.view = view;
    }

    @Override
    public PathRq build(SystemContext context) {
        String path = view.askFilePath(
                "Импорт заказа в CSV",
                config.getImportCsvOrdersPath()
        );

        return new PathRq(path);
    }
}
