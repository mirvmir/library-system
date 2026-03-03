package com.myapp.app.ui.console.presenter.implementations;

import com.myapp.app.ui.console.UiSession;
import com.myapp.app.ui.console.consoleViewModels.IView;
import com.myapp.app.ui.console.consoleViewModels.consoleEntity.*;
import com.myapp.app.useCases.adapter.ui.interfaces.IPresenter;
import com.myapp.app.useCases.services.outputs.*;

import java.util.ArrayList;
import java.util.List;

public class ConsolePresenter implements IPresenter {

    private final IView view;
    private final UiSession uiSession;

    public ConsolePresenter(IView view, UiSession uiSession) {
        this.view = view;
        this.uiSession = uiSession;
    }

    @Override
    public void present(Object response) {
        if (response instanceof AddBookToStockOutput rs) {
            view.displayMessage("Книга с isbn=" + rs.isbn() + " добавлена на  склад.");
            return;
        }
        if (response instanceof CalculateTotalEarningsByPeriodOutput rs) {
            view.displayMessage("Сумма заработанных средств: " + rs.totalEarnings() + ".");
            return;
        }
        if (response instanceof CancelOrderOutput rs) {
            view.displayMessage("Заказ отменён.");
            return;
        }
        if (response instanceof ChangeOrderStatusOutput rs) {
            view.displayMessage("Статус заказа изменён.");
            return;
        }
        if (response instanceof CompleteOrderOutput rs) {
            view.displayMessage("Заказ завершён.");
            return;
        }
        if (response instanceof CreateBookModelOutput rs) {
            view.displayMessage("Создана книга с isbn=" + rs.isbn() + ".");
            return;
        }
        if (response instanceof CreateBookRequestOutput rs) {
            view.displayMessage("Создан запрос на книгу, id: " + rs.requestId() + ".");
            return;
        }
        if (response instanceof CreateCustomerOutput rs) {
            CustomerViewModel newCustomer = new CustomerViewModel(rs.customerId());
            uiSession.setCurrentCustomer(newCustomer);
            return;
        }
        if (response instanceof CreateOrderOutput rs) {
            view.displayMessage("Создан новый заказ, id: " + rs.orderId() + ".");
            return;
        }
        if (response instanceof WriteOffBookOutput rs) {
            view.displayMessage("Книга с isbn=" + rs.isbn() + " списана.");
            return;
        }
        if (response instanceof GetOrdersOutput rs) {
            uiSession.setOrderSort(true);
            List<GetOrderOutput> ordersRs = rs.orders();
            List<OrderViewModel> orders = new ArrayList<>();

            for (GetOrderOutput orderRs : ordersRs) {
                orders.add(new OrderViewModel(
                        orderRs.id(),
                        orderRs.customerId(),
                        orderRs.status(),
                        orderRs.completionDate(),
                        orderRs.totalPrice(),
                        orderRs.isbns())
                );
            }

            uiSession.setOrders(orders);
            return;
        }
        if (response instanceof GetBooksOutput rs) {
            uiSession.setBookSort(true);
            List<GetBookOutput> booksRs = rs.books();
            List<BookViewModel> books = new ArrayList<>();

            for (GetBookOutput bookRs : booksRs) {
                books.add(new BookViewModel(
                        bookRs.isbn(),
                        bookRs.title(),
                        bookRs.author(),
                        bookRs.price(),
                        bookRs.available()
                        )
                );
            }

            uiSession.setBooks(books);
            return;
        }
        if (response instanceof GetBookRequestsOutput rs) {
            uiSession.setRequestSort(true);
            List<GetBookRequestOutput> bookRequestsRs = rs.bookRequests();
            List<BookRequestViewModel> requests = new ArrayList<>();

            for (GetBookRequestOutput bookRequestRs : bookRequestsRs) {
                requests.add(new BookRequestViewModel(
                        bookRequestRs.isbn(),
                        bookRequestRs.title(),
                        bookRequestRs.author(),
                        bookRequestRs.price(),
                        bookRequestRs.available(),
                        bookRequestRs.requestCount()
                        )
                );
            }

            uiSession.setRequests(requests);
            return;
        }
        if (response instanceof ExportBookModelOutput rs) {
            view.displayMessage("Книги экспортированы.");
            return;
        }
        if (response instanceof ExportOrderOutput rs) {
            view.displayMessage("Заказы экспортированы.");
            return;
        }
        if (response instanceof ImportBookModelOutput re) {
            view.displayMessage("Книги импортированы.");
            return;
        }
        if (response instanceof ImportOrderOutput rs) {
            view.displayMessage("Заказы импортированы.");
            return;
        }

        throw new IllegalArgumentException("Непредвиденная операция: " + response.getClass());
    }
}
