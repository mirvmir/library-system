package com.myapp.app.ui.console;

import java.util.Arrays;

import com.myapp.app.controllers.console.*;
import com.myapp.app.controllers.console.action.*;
import com.myapp.app.controllers.requests.*;
import com.myapp.app.ui.console.consoleViewModels.consoleEntity.CustomerViewModel;
import com.myapp.app.ui.console.menu.Menu;
import com.myapp.app.ui.console.menu.MenuItem;
import com.myapp.app.ui.console.menu.content.BooksMenuContent;
import com.myapp.app.ui.console.menu.content.BookRequestsMenuContent;
import com.myapp.app.ui.console.menu.hook.*;
import com.myapp.app.ui.console.menu.content.OrdersMenuContent;
import com.myapp.app.ui.console.requestBuilders.*;
import com.myapp.app.ui.console.requestBuilders.context.SortField;
import com.myapp.app.ui.console.requestBuilders.context.SortType;
import com.myapp.app.ui.console.requestBuilders.context.SystemContext;
import org.springframework.stereotype.Component;

@Component
public class MenuBuilder {

    private final WriteOffBookAction writeOffBookAction;
    private final AddBookToStockAction addBookToStockAction;
    private final CreateBookModelAction createBookModelAction;
    private final GetBookAction getBookAction;
    private final GetBookRequestAction getBookRequestAction;
    private final GetOrderAction getOrderAction;
    private final ExportBookModelCsvAction exportBookModelCsvAction;
    private final ImportBookModelCsvAction importBookModelCsvAction;
    private final CreateOrderAction createOrderAction;
    private final CancelOrderAction cancelOrderAction;
    private final CompleteOrderAction completeOrderAction;
    private final CalculateTotalEarningsByPeriodAction calculateTotalEarningsByPeriodAction;
    private final ExportOrderCsvAction exportOrderCsvAction;
    private final ImportOrderCsvAction importOrderCsvAction;
    private final CreateBookRequestAction createBookRequestAction;

    private final WriteOffBookRqBuilder writeOffBookRqBuilder;
    private final AddBookToStockRequestBuilder addBookToStockRequestBuilder;
    private final CreateBookModelRequestBuilder createBookModelRequestBuilder;
    private final GetBookRequestBuilder getBookRequestBuilder;
    private final GetBookRequestRequestBuilder getBookRequestRequestBuilder;
    private final GetOrderRequestBuilder getOrderRequestBuilder;
    private final ExportBookModelCsvRequestBuilder exportBookModelCsvRequestBuilder;
    private final ImportBookModelCsvRequestBuilder importBookModelCsvRequestBuilder;
    private final CreateOrderRequestBuilder createOrderRequestBuilder;
    private final CancelOrderRequestBuilder cancelOrderRequestBuilder;
    private final CompleteOrderRequestBuilder completeOrderRequestBuilder;
    private final CalculateTotalEarningsByPeriodRequestBuilder calculateTotalEarningsByPeriodRequestBuilder;
    private final ExportOrderCsvRequestBuilder exportOrderCsvRequestBuilder;
    private final ImportOrderCsvRequestBuilder importOrderCsvRequestBuilder;
    private final CreateBookRequestRequestBuilder createBookRequestRequestBuilder;

    private final BooksBeforeRenderHook booksBeforeRenderHook;
    private final BooksMenuContent booksMenuContent;
    private final OrdersBeforeRenderHook ordersBeforeRenderHook;
    private final OrdersMenuContent ordersMenuContent;
    private final BookRequestsBeforeRenderHook bookRequestsBeforeRenderHook;
    private final BookRequestsMenuContent bookRequestsMenuContent;

    private Menu rootMenu = null;

    public MenuBuilder(WriteOffBookAction writeOffBookAction,
                       AddBookToStockAction addBookToStockAction,
                       CreateBookModelAction createBookModelAction,
                       GetBookAction getBookAction,
                       GetBookRequestAction getBookRequestAction,
                       GetOrderAction getOrderAction,
                       ExportBookModelCsvAction exportBookModelCsvAction,
                       ImportBookModelCsvAction importBookModelCsvAction,
                       CreateOrderAction createOrderAction,
                       CancelOrderAction cancelOrderAction,
                       CompleteOrderAction completeOrderAction,
                       CalculateTotalEarningsByPeriodAction calculateTotalEarningsByPeriodAction,
                       ExportOrderCsvAction exportOrderCsvAction,
                       ImportOrderCsvAction importOrderCsvAction,
                       CreateBookRequestAction createBookRequestAction,
                       WriteOffBookRqBuilder writeOffBookRqBuilder,
                       AddBookToStockRequestBuilder addBookToStockRequestBuilder,
                       CreateBookModelRequestBuilder createBookModelRequestBuilder,
                       GetBookRequestBuilder getBookRequestBuilder,
                       GetBookRequestRequestBuilder getBookRequestRequestBuilder,
                       GetOrderRequestBuilder getOrderRequestBuilder,
                       ExportBookModelCsvRequestBuilder exportBookModelCsvRequestBuilder,
                       ImportBookModelCsvRequestBuilder importBookModelCsvRequestBuilder,
                       CreateOrderRequestBuilder createOrderRequestBuilder,
                       CancelOrderRequestBuilder cancelOrderRequestBuilder,
                       CompleteOrderRequestBuilder completeOrderRequestBuilder,
                       CalculateTotalEarningsByPeriodRequestBuilder calculateTotalEarningsByPeriodRequestBuilder,
                       ExportOrderCsvRequestBuilder exportOrderCsvRequestBuilder,
                       ImportOrderCsvRequestBuilder importOrderCsvRequestBuilder,
                       CreateBookRequestRequestBuilder createBookRequestRequestBuilder,
                       BooksBeforeRenderHook booksBeforeRenderHook,
                       BooksMenuContent booksMenuContent,
                       OrdersBeforeRenderHook ordersBeforeRenderHook,
                       OrdersMenuContent ordersMenuContent,
                       BookRequestsBeforeRenderHook bookRequestsBeforeRenderHook,
                       BookRequestsMenuContent bookRequestsMenuContent) {
        this.writeOffBookAction = writeOffBookAction;
        this.addBookToStockAction = addBookToStockAction;
        this.createBookModelAction = createBookModelAction;
        this.getBookAction = getBookAction;
        this.getBookRequestAction = getBookRequestAction;
        this.getOrderAction = getOrderAction;
        this.exportBookModelCsvAction = exportBookModelCsvAction;
        this.importBookModelCsvAction = importBookModelCsvAction;
        this.createOrderAction = createOrderAction;
        this.cancelOrderAction = cancelOrderAction;
        this.completeOrderAction = completeOrderAction;
        this.calculateTotalEarningsByPeriodAction = calculateTotalEarningsByPeriodAction;
        this.exportOrderCsvAction = exportOrderCsvAction;
        this.importOrderCsvAction = importOrderCsvAction;
        this.createBookRequestAction = createBookRequestAction;
        this.writeOffBookRqBuilder = writeOffBookRqBuilder;
        this.addBookToStockRequestBuilder = addBookToStockRequestBuilder;
        this.createBookModelRequestBuilder = createBookModelRequestBuilder;
        this.getBookRequestBuilder = getBookRequestBuilder;
        this.getBookRequestRequestBuilder = getBookRequestRequestBuilder;
        this.getOrderRequestBuilder = getOrderRequestBuilder;
        this.exportBookModelCsvRequestBuilder = exportBookModelCsvRequestBuilder;
        this.importBookModelCsvRequestBuilder = importBookModelCsvRequestBuilder;
        this.createOrderRequestBuilder = createOrderRequestBuilder;
        this.cancelOrderRequestBuilder = cancelOrderRequestBuilder;
        this.completeOrderRequestBuilder = completeOrderRequestBuilder;
        this.calculateTotalEarningsByPeriodRequestBuilder = calculateTotalEarningsByPeriodRequestBuilder;
        this.exportOrderCsvRequestBuilder = exportOrderCsvRequestBuilder;
        this.importOrderCsvRequestBuilder = importOrderCsvRequestBuilder;
        this.createBookRequestRequestBuilder = createBookRequestRequestBuilder;
        this.booksBeforeRenderHook = booksBeforeRenderHook;
        this.booksMenuContent = booksMenuContent;
        this.ordersBeforeRenderHook = ordersBeforeRenderHook;
        this.ordersMenuContent = ordersMenuContent;
        this.bookRequestsBeforeRenderHook = bookRequestsBeforeRenderHook;
        this.bookRequestsMenuContent = bookRequestsMenuContent;
    }

    public void buildMenu(CustomerViewModel customer) {
        Menu booksMenu = buildBooksMenu(customer);
        Menu ordersMenu = buildOrdersMenu(customer);
        Menu requestsMenu = buildRequestsMenu(customer);

        this.rootMenu = buildRootMenu(customer, booksMenu, ordersMenu, requestsMenu);
    }

    public Menu getRootMenu() {
        return this.rootMenu;
    }

    private Menu buildBooksMenu(CustomerViewModel customer) {
        return new Menu("Книги", Arrays.asList(
                item("Списать книгу со склада",
                        writeOffBookAction,
                        writeOffBookRqBuilder,
                        emptyCtx()
                ),

                item("Добавить книгу на склад",
                        addBookToStockAction,
                        addBookToStockRequestBuilder,
                        emptyCtx()
                ),

                item("Добавить книгу в электронную библиотеку",
                        createBookModelAction,
                        createBookModelRequestBuilder,
                        emptyCtx()
                ),

                item("Список книг (по алфавиту)",
                        getBookAction,
                        getBookRequestBuilder,
                        ctx(null, SortField.TITLE, SortType.BOOK)
                ),

                item("Список книг (по цене)",
                        getBookAction,
                        getBookRequestBuilder,
                        ctx(null, SortField.PRICE, SortType.BOOK)
                ),

                item("Список книг (по наличию на складе)",
                        getBookAction,
                        getBookRequestBuilder,
                        ctx(null, SortField.AVAILABILITY, SortType.BOOK)
                ),


                item("Список «залежавшихся» книг (по дате поступления)",
                        getBookAction,
                        getBookRequestBuilder,
                        ctx(null, SortField.DELIVERY_DATE, SortType.STALE_BOOK)
                ),

                item("Список «залежавшихся» книг (по цене)",
                        getBookAction,
                        getBookRequestBuilder,
                        ctx(null, SortField.PRICE, SortType.STALE_BOOK)
                ),

                item("Создать заказ",
                        createOrderAction,
                        createOrderRequestBuilder,
                        emptyCtx()
                ),

                item("Экспортировать модели книг в CSV",
                        exportBookModelCsvAction,
                        exportBookModelCsvRequestBuilder,
                        emptyCtx()
                ),

                item("Импортировать модели книг из CSV",
                        importBookModelCsvAction,
                        importBookModelCsvRequestBuilder,
                        emptyCtx()
                ),
                back(ctx(customer.getId())))
        )
                .setBeforeRender(booksBeforeRenderHook)
                .setContent(booksMenuContent);
    }

        // КОМАНДЫ ДЛЯ  ЗАКАЗОВ
    private Menu buildOrdersMenu(CustomerViewModel customer) {
        return new Menu("Заказы", Arrays.asList(
                item("Список заказов (по дате исполнения)",
                        getOrderAction,
                        getOrderRequestBuilder,
                        ctx(null, SortField.COMPLETION_DATE, SortType.ORDER)
                ),

                item("Список заказов (по цене)",
                        getOrderAction,
                        getOrderRequestBuilder,
                        ctx(null, SortField.PRICE, SortType.ORDER)
                ),

                item("Список заказов (по статусу)",
                        getOrderAction,
                        getOrderRequestBuilder,
                        ctx(null, SortField.STATUS, SortType.ORDER)
                ),

                item("Отменить заказ",
                        cancelOrderAction,
                        cancelOrderRequestBuilder,
                        emptyCtx()
                ),


                item("Выполнить заказ",
                        completeOrderAction,
                        completeOrderRequestBuilder,
                        emptyCtx()
                ),

                item("Список выполненных заказов за период времени (по дате)",
                        getOrderAction,
                        getOrderRequestBuilder,
                        ctx(null, SortField.COMPLETION_DATE, SortType.COMPLETED_ORDER)
                ),

                item("Список выполненных заказов за период времени (по цене)",
                        getOrderAction,
                        getOrderRequestBuilder,
                        ctx(
                                null,
                                SortField.PRICE,
                                SortType.COMPLETED_ORDER
                        )
                ),

                item("Сумма заработанных средств за период времени",
                        calculateTotalEarningsByPeriodAction,
                        calculateTotalEarningsByPeriodRequestBuilder,
                        emptyCtx()
                ),

                item("Экспортировать заказы в CSV",
                        exportOrderCsvAction,
                        exportOrderCsvRequestBuilder,
                        emptyCtx()
                ),

                item("Импортировать заказы из CSV",
                        importOrderCsvAction,
                        importOrderCsvRequestBuilder,
                        emptyCtx()
                ),

                back(ctx(customer.getId())))
        )
                .setBeforeRender(ordersBeforeRenderHook)
                .setContent(ordersMenuContent);
    }

    private Menu buildRequestsMenu(CustomerViewModel customer) {
        // КОМАНДЫ ДЛЯ  ЗАПРОСОВ
        return new Menu("Запросы", Arrays.asList(
                item("Оставить запрос на книгу",
                        createBookRequestAction,
                        createBookRequestRequestBuilder,
                        ctx(customer.getId(), null, null)
                ),

                item("Список запросов на книгу (по количеству запросов)",
                        getBookRequestAction,
                        getBookRequestRequestBuilder,
                        ctx(null, SortField.COUNT, SortType.REQUEST)
                ),

                item("Список запросов на книгу (по алфавиту)",
                        getBookRequestAction,
                        getBookRequestRequestBuilder,
                        ctx(null, SortField.TITLE, SortType.REQUEST)
                ),

                back(ctx(customer.getId())))
        )
                .setBeforeRender(bookRequestsBeforeRenderHook)
                .setContent(bookRequestsMenuContent);
    }

    private Menu buildRootMenu(CustomerViewModel customer,
                               Menu booksMenu,
                               Menu ordersMenu,
                               Menu requestsMenu) {
        return new Menu("Главное меню", Arrays.asList(
                new MenuItem<Void>("Книги",
                        (IAction<Void>) null,
                        (RequestBuilder<Void>) null,
                        ctx(customer.getId()),
                        booksMenu),

                new MenuItem<Void>("Заказы",
                        (IAction<Void>) null,
                        (RequestBuilder<Void>) null,
                        ctx(customer.getId()),
                        ordersMenu),

                new MenuItem<Void>("Запросы",
                        (IAction<Void>) null,
                        (RequestBuilder<Void>) null,
                        ctx(customer.getId()),
                        requestsMenu),

                new MenuItem<Void>("Выход",
                        (IAction<Void>) null,
                        (RequestBuilder<Void>) null,
                        null,
                        null,
                        true)));
    }

    private SystemContext ctx(Long customerId) {
        return new SystemContext(customerId, null, null);
    }

    private SystemContext ctx(Long customerId, SortField field, SortType type) {
        return new SystemContext(customerId, field, type);
    }

    private SystemContext emptyCtx() {
        return new SystemContext(null, null, null);
    }

    private <T> MenuItem<T> item(String title,
                                 IAction<T> action,
                                 RequestBuilder<T> rb,
                                 SystemContext ctx) {
        return new MenuItem<>(
                title,
                action,
                rb,
                ctx,
                null);
    }

    private MenuItem<Void> back(SystemContext ctx) {
        return new MenuItem<>(
                "← Назад",
                null,
                null,
                ctx,
                this.getRootMenu()
        );
    }
}
