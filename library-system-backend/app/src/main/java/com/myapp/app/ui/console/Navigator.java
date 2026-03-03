package com.myapp.app.ui.console;

import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.menu.Menu;
import org.springframework.stereotype.Component;

@Component
public class Navigator {
    private final IView view;

    public Navigator(IView view) {
        this.view = view;
    }

    public IView getView() {
        return this.view;
    }

    public int getUserChoice() {
        return this.view.askForInt("Выберите пункт меню: ") - 1;
    }

    public void renderMenuScreen(Menu menu, Long customerId) {
        view.clear();
        view.displayMessage("Меню: " + menu.getName()
                + "\nПользователь: " + customerId);

        if (menu.getContent() != null) {
            menu.getContent().render();
        }

        printMenu(menu);
    }

    private void printMenu(Menu currentMenu) {
        this.view.displayMenu(currentMenu);
    }
}
