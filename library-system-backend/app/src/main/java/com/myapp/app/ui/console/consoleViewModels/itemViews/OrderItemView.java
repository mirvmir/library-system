package com.myapp.app.ui.console.consoleViewModels.itemViews;

import com.myapp.app.ui.console.consoleViewModels.consoleEntity.OrderViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrderItemView implements IItemView<OrderViewModel> {

    @Override
    public List<String[]> viewList(List<OrderViewModel> orders) {
        List<String[]> rows = new ArrayList<>();

        if (orders != null && !orders.isEmpty()) {
            System.out.println("\nСписок заказов");
            System.out.println("-".repeat(40));

            rows.add(new String[]{"№", "Сумма", "Дата выполнения", "Статус"});

            for (OrderViewModel order : orders) {
                rows.add(
                        new String[]{
                                order.getId().toString(),
                                String.valueOf(order.getTotalPrice()),
                                order.getCompletionDate() != null
                                        ? order.getCompletionDate().toString()
                                        : "-",
                                order.getStatus()
                        }
                );
            }
        } else {
            rows.add(new String[] {
                    "Нет заказов"
            });
        }

        return rows;
    }

    @Override
    public StringBuilder viewItem(OrderViewModel order) {
        StringBuilder sb = new StringBuilder();

        sb.append("Номер заказа: ").append(order.getId());
        sb.append("\nНомер заказчика: ").append(order.getCustomerId());
        sb.append("\nКниги в заказе:\n");

        for (String isbn : order.getIsbns()) {
            sb.append("\t").append(isbn).append("\n");
        }

        sb.append("Итого: ").append(order.getTotalPrice()).append(" руб");

        return sb;
    }
}
