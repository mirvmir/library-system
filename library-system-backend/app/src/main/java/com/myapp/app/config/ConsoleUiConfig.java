package com.myapp.app.config;

import com.myapp.app.ui.console.UiSession;
import com.myapp.app.ui.console.consoleViewModels.ConsoleView;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.presenter.implementations.ConsolePresenter;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("console")
@ComponentScan({
        "com.myapp.app.ui.console",
        "com.myapp.app.controllers.console"
})
public class ConsoleUiConfig {

    @Bean
    public IView view() {
        return new ConsoleView();
    }

    @Bean
    public IPresenter presenter(IView view, UiSession uiSession) {
        return new ConsolePresenter(view, uiSession);
    }
}
