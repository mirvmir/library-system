package com.myapp.app.ui.console.consoleViewModels.itemViews;

import com.myapp.app.ui.console.consoleViewModels.consoleEntity.BookViewModel;

import java.util.ArrayList;
import java.util.List;

public class BookItemView implements IItemView<BookViewModel> {

    @Override
    public List<String[]> viewList(List<BookViewModel> books) {
        List<String[]> rows = new ArrayList<>();

        if (books != null && !books.isEmpty()) {
            System.out.println("\nСписок книг");
            System.out.println("-".repeat(40));

            rows.add(new String[]{"ISBN", "Название", "Цена", "Наличие"});

            int i = 1;
            for (BookViewModel book : books) {
                rows.add(new String[]{
                        book.getIsbn(),
                        book.getTitle(),
                        String.valueOf(book.getPrice()),
                        book.isAvailable() ? "В наличии" : "Отсутствует",
                });
            }
        } else {
            rows.add(new String[]{"Нет книг"});
        }

        return rows;
    }

    @Override
    public StringBuilder viewItem(BookViewModel book) {
        StringBuilder sb = new StringBuilder();

        sb.append("ISBN: ").append(book.getIsbn());
        sb.append("\nНазвание: ").append(book.getTitle());
        sb.append("\nЦена: ").append(book.getPrice());
        sb.append("\nСтатус: ").append(book.isAvailable() ? "В наличии" : "Отсутствует");

        return sb;
    }
}
