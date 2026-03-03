package com.myapp.app.ui.console;

import com.myapp.app.controllers.console.action.CreateCustomerAction;
import com.myapp.app.controllers.requests.EmptyRq;
import com.myapp.app.exception.AppException;
import com.myapp.app.exception.business.BusinessException;
import com.myapp.app.exception.db.DbConnectionException;
import com.myapp.app.exception.db.DbTransactionException;
import com.myapp.app.ui.console.consoleViewModels.consoleEntity.CustomerViewModel;
import com.myapp.app.ui.console.menu.Menu;
import com.myapp.app.ui.console.menu.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MenuController {

    private static final Logger log = LoggerFactory.getLogger(MenuController.class);

    private final MenuBuilder builder;
    private final Navigator navigator;
    private final ErrorHandler handler;
    private final UiSession uiSession;

    private final CreateCustomerAction createCustomerAction;

    public MenuController(MenuBuilder builder,
                          Navigator navigator,
                          ErrorHandler handler,
                          UiSession uiSession,
                          CreateCustomerAction createCustomerAction) {
        this.builder = builder;
        this.navigator = navigator;
        this.handler = handler;
        this.uiSession = uiSession;
        this.createCustomerAction = createCustomerAction;
    }

    public void run() throws AppException {
        try {
            createCustomerAction.execute(new EmptyRq());
        } catch (DbConnectionException e) {
            log.error("Failed start: cannot create guest user due to DB connection problem", e);
            navigator.getView().displayError("Нет соединения с БД. Похоже, сервер недоступен.");
            return;
        } catch (DbTransactionException e) {
            log.error("Failed start: cannot create guest user due to DB problem", e);
            navigator.getView().displayError("Создание пользователя не было выполнено. "
                    + "Похоже, БД недоступна");
            return;
        }

        CustomerViewModel currentCustomer = uiSession.getCurrentCustomer();

        if (null == currentCustomer) {
            navigator.getView().displayError("Пользователь не был создан.");
            return;
        }

        builder.buildMenu(currentCustomer);

        Menu currentMenu = builder.getRootMenu();

        while (true) {
            if (currentMenu.getBeforeRender() != null) {
                currentMenu.getBeforeRender().run();
            }

            navigator.renderMenuScreen(currentMenu, currentCustomer.getId());
            int cmd = navigator.getUserChoice();

            while (cmd < 0 || cmd >= currentMenu.getMenuItems().size()) {
                cmd = navigator.getUserChoice();
            }

            MenuItem item = currentMenu.getMenuItems().get(cmd);
            if (null == item) {
                log.error("MenuItem is null: userId={}, menu={}, index={}",
                        currentCustomer.getId(), safeMenuTitle(currentMenu), cmd);
                throw new IllegalStateException("MenuItem is null at index=" + cmd);
            }

            try {
                if (item.isExitsProgram()) {
                    log.info("Session ended by user with ID={}", currentCustomer.getId());
                    // factory.saveState(); // если всё перешло в БД, то нечего сохранять
                    break;
                }

                if (item.getNextMenu() != null) {
                    currentMenu = item.getNextMenu();
                } else if (item.getAction() != null) {
                    log.info("Action started: userId={}, menu='{}', item='{}'",
                            currentCustomer.getId(), safeMenuTitle(currentMenu), safeItemTitle(item));

                    var rq = item.getRequestBuilder().build(item.getContext());
                    item.getAction().execute(rq);

                    log.info("Action finished: userId={}, item='{}'",
                            currentCustomer.getId(), safeItemTitle(item));

                    navigator.getView().pause();
                } else {
                    currentMenu = builder.getRootMenu();
                }
            } catch (DbConnectionException e) {
                log.warn("DB connection error during action: userId={}, item='{}'",
                        currentCustomer.getId(), safeItemTitle(item), e);
                handler.handle(e);
            } catch (DbTransactionException e) {
                handler.handle(e);
            } catch (IllegalStateException | BusinessException e) {
                navigator.getView().displayError(e.getMessage());
            } catch (RuntimeException e) {
                log.error("Unexpected runtime error: userId={}, menu='{}', item='{}'",
                        currentCustomer.getId(), safeMenuTitle(currentMenu), safeItemTitle(item), e);
                handler.handle(e);
            }
        }
    }

    private String safeMenuTitle(Menu menu) {
        return null == menu ? "<null>" : menu.getName();
    }

    private String safeItemTitle(MenuItem<?> menuItem) {
        return null == menuItem ? "<null>" : menuItem.getTitle();
    }
}