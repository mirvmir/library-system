package com.myapp.app;

import com.myapp.app.useCases.services.interfaces.*;
import com.myapp.app.useCases.services.inputs.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class TestDataSeeder {
    private final CreateCustomerService customerService;
    private final CreateBookModelService bookModelService;
    private final AddBookToStockService bookUnitService;
    private final CreateOrderService orderService;
    private final CreateBookRequestService requestService;

    public TestDataSeeder(CreateCustomerService customerService, CreateBookModelService bookModelService, AddBookToStockService bookUnitService, CreateOrderService orderService, CreateBookRequestService requestService) {
        this.customerService = customerService;
        this.bookModelService = bookModelService;
        this.bookUnitService = bookUnitService;
        this.orderService = orderService;
        this.requestService = requestService;
    }

    @PostConstruct
    public void seed() {
        customerService.execute(new CreateCustomerInput());

        bookModelService.execute(new CreateBookModelInput("978-5-389-24797-0", "Сёгун", "Джеймс Клавелл", BigDecimal.valueOf(480)));
        bookModelService.execute(new CreateBookModelInput("978-5-04-122102-7", "Над пропостью во ржи", "Сэлинджер Джером Дэвид", BigDecimal.valueOf(478)));
        bookModelService.execute(new CreateBookModelInput("978-5-17-119245-7", "Сто лет одиночества", "Маркес Габриэль Гарсиа", BigDecimal.valueOf(979)));
        bookModelService.execute(new CreateBookModelInput("978-5-389-19086-3", "Анна Каренина", "Толстой Лев Николаевич", BigDecimal.valueOf(341)));
        bookModelService.execute(new CreateBookModelInput("978-5-4461-0772-8", "Чистая архитектура", "Роберт Мартин", BigDecimal.valueOf(865)));
        bookModelService.execute(new CreateBookModelInput("978-5-17-175640-6", "Сага о Форсайтах", "Голсуорси Джон", BigDecimal.valueOf(519)));

        bookUnitService.execute(new AddBookToStockInput("978-5-389-19086-3"));
        bookUnitService.execute(new AddBookToStockInput("978-5-17-119245-7"));
        bookUnitService.execute(new AddBookToStockInput("978-5-17-175640-6"));
        bookUnitService.execute(new AddBookToStockInput("978-5-17-175640-6"));
        bookUnitService.execute(new AddBookToStockInput("978-5-17-175640-6"));
        bookUnitService.execute(new AddBookToStockInput("978-5-389-24797-0"));
        bookUnitService.execute(new AddBookToStockInput("978-5-389-24797-0"));

        orderService.execute(new CreateOrderInput(1L, Arrays.asList("978-5-17-119245-7")));
        orderService.execute(new CreateOrderInput(1L, Arrays.asList("978-5-389-24797-0",  "978-5-4461-0772-8")));

        requestService.execute(new CreateBookRequestInput("978-5-389-19086-3", 1L));
        requestService.execute(new CreateBookRequestInput("978-5-4461-0772-8", 1L));
    }
}
