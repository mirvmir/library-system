package com.myapp.app.ui.console;

import com.myapp.app.exception.AppException;
import com.myapp.app.exception.db.DbConnectionException;
import com.myapp.app.exception.db.DbTransactionException;
import com.myapp.app.ui.console.consoleViewModels.IView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    private final IView view;

    public ErrorHandler(IView view) {
        this.view = view;
    }

    public void handle(Throwable t) {
        if (t instanceof DbConnectionException e) {
            log.error("DbConnectionException error", e);
            return;
        } else if (t instanceof DbTransactionException e) {
            view.displayError(e.getMessage() + " Изменения отменены.");
            return;
        } else if (t instanceof AppException e) {
            log.error("Application error", e);
            return;
        }
        log.error("Unexpected error", t);
        view.displayError("Произошла непредвиденная ошибка.");
    }
}