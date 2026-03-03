package com.myapp.app.ui.console.consoleViewModels;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.myapp.app.ui.console.menu.Menu;

public interface IView {
    void displayMenu(Menu var1);

    <T> void displayItem(T item);

    <T> void displayItems(List<T> items);

    void displayMessage(String msg);

    void displayError(String msg);

    int askForInt(String msg);

    Long askForLong(String msg);

    List<String> askForListIsbn();

    String askForString(String msg);

    BigDecimal askForBigDecimal(String msg);

    String askFilePath(String actionDescription, String defaultPath);

    LocalDateTime askForStartDateTime(String msg);

    LocalDateTime askForEndDateTime(String msg);

    void clear();

    void pause();
}
