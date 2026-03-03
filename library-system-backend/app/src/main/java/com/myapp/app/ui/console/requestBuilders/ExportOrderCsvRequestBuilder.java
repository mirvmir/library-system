package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.frameworks.Config;
import com.myapp.app.controllers.requests.PathRq;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

@Component
public class ExportOrderCsvRequestBuilder implements RequestBuilder<PathRq> {

    private final Config config;
    private final IView view;

    public ExportOrderCsvRequestBuilder(Config config, IView view) {
        this.config = config;
        this.view = view;
    }

    @Override
    public PathRq build(SystemContext context) {
        String path = view.askFilePath(
                "Экспорт заказов в CSV",
                config.getExportCsvOrdersPath()
        );

        return new PathRq(path);
    }
}
