package com.myapp.app.ui.console.requestBuilders;

import com.myapp.app.controllers.requests.PathRq;
import com.myapp.app.frameworks.Config;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

@Component
public class ExportBookModelCsvRequestBuilder implements RequestBuilder<PathRq> {

    private final Config config;
    private final IView view;

    public ExportBookModelCsvRequestBuilder(Config config, IView view) {
        this.config = config;
        this.view = view;
    }

    @Override
    public PathRq build(SystemContext context) {
        String path = view.askFilePath(
                "Экспорт книг в CSV",
                config.getExportCsvModelsPath()
        );

        return new PathRq(path);
    }
}
