package com.myapp.app;

import com.myapp.app.exception.AppException;
import com.myapp.app.ui.console.ErrorHandler;
import com.myapp.app.ui.console.MenuController;
import com.myapp.app.ui.console.consoleViewModels.ConsoleView;
import com.myapp.app.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        try {
            DbMigrate.migrate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        AnnotationConfigApplicationContext configContext = new AnnotationConfigApplicationContext();

        configContext.getEnvironment().setActiveProfiles("console");
        configContext.register(AppConfig.class);
        configContext.refresh();

        MenuController controller = configContext.getBean(MenuController.class);
        ErrorHandler handler = configContext.getBean(ErrorHandler.class);
        ConsoleView view = configContext.getBean(ConsoleView.class);
        try {
            controller.run();
        } catch (AppException e) {
            handler.handle(e);
            view.pause();
            System.exit(1);
        }

        configContext.close();
    }
}