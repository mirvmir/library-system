package com.myapp.app.ui.console.consoleViewModels;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.myapp.app.ui.console.consoleViewModels.consoleEntity.*;
import com.myapp.app.ui.console.consoleViewModels.itemViews.*;
import com.myapp.app.ui.console.menu.Menu;
import com.myapp.app.ui.console.menu.MenuItem;

public class ConsoleView implements IView {

    private final Map<Class<?>, IItemView<?>> itemViews = new HashMap<>();
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
        itemViews.put(BookViewModel.class, new BookItemView());
        itemViews.put(BookRequestViewModel.class, new BookRequestItemView());
        itemViews.put(OrderViewModel.class, new OrderItemView());
    }

    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // МЕТОДЫ ДЛЯ ВЫВОДА
    public void displayMenu(Menu currentMenu) {
        System.out.println("-".repeat(40));
//        System.out.println("> Меню: " + currentMenu.getName());
        List<MenuItem> items = currentMenu.getMenuItems();

        for (int i = 0; i < items.size(); ++i) {
            System.out.println("\t[" + (i + 1) + "] " + ((MenuItem<?>) items.get(i)).getTitle());
        }

        System.out.println("-".repeat(40));
    }

    @Override
    public <T> void displayItem(T item) {
        if (null == item) {
            System.out.println("Нет элементов");
            return;
        }

        Class<T> type = (Class<T>) item.getClass();
        IItemView<T> view = findView(type);
        System.out.println(view.viewItem(item));
    }

    @Override
    public <T> void displayItems(List<T> items) {
        if (null == items || items.isEmpty()) {
            System.out.println("Нет данных");
            return;
        }

        Class<T> type = (Class<T>) items.get(0).getClass();
        IItemView<T> view = findView(type);

        List<String[]> rows = view.viewList(items);
        printTable(rows);
    }

    public void displayMessage(String msg) {
        System.out.println();
        System.out.println(msg);
    }

    public void pause() {
        System.out.println("\nОжидаю Enter...");
        scanner.nextLine();
    }

    public void displayError(String msg) {
        System.out.println("Ошибка: " + msg);
    }

    // МЕТОДЫ ДЛЯ ВВОДА
    @Override
    public int askForInt(String msg) {
        System.out.println(msg);

        while (true) {
            String input = this.scanner.nextLine().trim();
            if (input.matches("\\d+")) {
                return Integer.parseInt(input);
            }

            System.out.println("Введите корректное число:");
        }
    }

    @Override
    public Long askForLong(String msg) {
        System.out.println(msg);

        while (true) {
            String input = this.scanner.nextLine().trim();
            if (input.matches("\\d+")) {
                return Long.parseLong(input);
            }

            System.out.println("Введите корректное число:");
        }
    }

    @Override
    public List<String> askForListIsbn() {
        System.out.println("Введите isbn книг через запятую:");

        String input = this.scanner.nextLine().trim();
        List<String> selectedString = new ArrayList<>();

        for (String part : input.split(",")) {
            part = part.trim();

            if (part.matches("\\d+")) {
                if (!selectedString.contains(part)) {
                    selectedString.add(part);
                }
            }
        }

        if (selectedString.isEmpty()) {
            this.displayMessage("Ни одной книги не выбрано.");
            return Collections.emptyList();
        }

        return selectedString;
    }

    @Override
    public String askForString(String msg) {
        System.out.println(msg);

        while (true) {
            String input = this.scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Строка не может быть пустой. Повторите ввод:");
        }
    }

    @Override
    public BigDecimal askForBigDecimal(String msg) {
        System.out.println(msg);

        while (true) {
            String input = this.scanner.nextLine().trim();

            input = input.replace(',', '.');

            if (input.matches("\\d+(\\.\\d+)?")) {
                return new BigDecimal(input);
            }

            System.out.println("Введите корректное число: ");
        }
    }

    @Override
    public LocalDateTime askForStartDateTime(String msg) {
        while (true) {
            try {
                System.out.println(msg + " (пример: 24.10.2025):");
                String input = this.scanner.nextLine().trim();

                String[] parts = input.split("\\.");
                if (parts.length != 3) {
                    System.out.println("Неверный формат. Введите дату в виде: 24.10.2025");
                    continue;
                }

                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                LocalDate date = LocalDate.of(year, month, day);
                LocalDateTime start = date.atStartOfDay();

                System.out.println("Дата начала установлена: " + start);
                return start;
            } catch (Exception e) {
                System.out.println("Некорректная дата, попробуйте снова.");
            }
        }
    }

    @Override
    public LocalDateTime askForEndDateTime(String msg) {
        while (true) {
            try {
                System.out.println(msg + " (пример: 24.10.2025):");
                String input = this.scanner.nextLine().trim();

                String[] parts = input.split("\\.");
                if (parts.length != 3) {
                    System.out.println("Неверный формат. Введите дату в виде: 24.10.2025");
                    continue;
                }

                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);

                LocalDate date = LocalDate.of(year, month, day);
                LocalDateTime end = date.atTime(23, 59, 59);

                System.out.println("Дата конца установлена: " + end);
                return end;
            } catch (Exception e) {
                System.out.println("Некорректная дата, попробуйте снова.");
            }
        }
    }

    @Override
    public String askFilePath(String actionDescription, String defaultPath) {
        System.out.println(actionDescription);
        System.out.println("Введите путь к файлу (Нажмите Enter, чтобы использовать: " + defaultPath + "):");

        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return defaultPath;
        }

        return input;
    }

    private <T> IItemView<T> findView(Class<T> type)
            throws IllegalArgumentException {
        IItemView<?> i = itemViews.get(type);
        if (null == i) {
            throw new IllegalArgumentException("Нет обработки типа: " + type.getSimpleName());
        }
        return (IItemView<T>) i;
    }

    private void printTable(List<String[]> rows) {
        if (rows != null && !rows.isEmpty()) {
            int columns = ((String[]) rows.getFirst()).length;
            int[] widths = new int[columns];

            for (String[] row : rows) {
                for (int i = 0; i < columns; ++i) {
                    if (row[i] != null && row[i].length() > widths[i]) {
                        widths[i] = row[i].length();
                    }
                }
            }

            for (String[] row : rows) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < columns; ++i) {
                    String cell = row[i] == null ? "" : row[i];
                    sb.append(cell);
                    int spaces = widths[i] - cell.length() + 3;
                    sb.append(" ".repeat(Math.max(spaces, 1)));
                }

                System.out.println(sb);
            }
        }
    }
}
