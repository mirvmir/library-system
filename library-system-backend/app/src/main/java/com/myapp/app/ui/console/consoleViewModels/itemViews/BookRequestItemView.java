package com.myapp.app.ui.console.consoleViewModels.itemViews;

import com.myapp.app.ui.console.consoleViewModels.consoleEntity.BookRequestViewModel;

import java.util.ArrayList;
import java.util.List;

public class BookRequestItemView implements IItemView<BookRequestViewModel> {

    @Override
    public List<String[]> viewList(List<BookRequestViewModel> requests) {
        List<String[]> rows = new ArrayList<>();

        if (requests != null && !requests.isEmpty()) {
            System.out.println("\nСписок запросов");
            System.out.println("-".repeat(40));

            rows.add(new String[]{"ISBN", "Название", "Цена", "Количество запросов"});

            int i = 1;
            for (BookRequestViewModel request : requests) {
                rows.add(new String[]{
                        request.getIsbn(),
                        request.getTitle(),
                        String.valueOf(request.getPrice()),
                        String.valueOf(request.getRequestCount())
                });
            }
        } else {
            rows.add(new String[]{"Нет книг"});
        }

        return rows;
    }

    @Override
    public StringBuilder viewItem(BookRequestViewModel request) {
        StringBuilder sb = new StringBuilder();

        sb.append("ISBN: ").append(request.getIsbn());
        sb.append("\nНазвание: ").append(request.getTitle());
        sb.append("\nЦена: ").append(request.getPrice());
        sb.append("\nКоличество запросов: ").append(request.getRequestCount());

        return sb;
    }
}
