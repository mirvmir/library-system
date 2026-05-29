package io.github.mirvmir;

import io.github.mirvmir.useCases.services.inputs.AddBookToStockInput;
import io.github.mirvmir.useCases.services.inputs.CreateBookModelInput;
import io.github.mirvmir.useCases.services.inputs.CreateBookRequestInput;
import io.github.mirvmir.useCases.services.interfaces.AddBookToStockService;
import io.github.mirvmir.useCases.services.interfaces.CreateBookModelService;
import io.github.mirvmir.useCases.services.interfaces.CreateBookRequestService;
import io.github.mirvmir.useCases.services.interfaces.CreateOrderService;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;

@Component
public class TestDataSeeder {
    //private final CreateCustomerService customerService;
    private final CreateBookModelService bookModelService;
    private final AddBookToStockService bookUnitService;
    private final CreateBookRequestService requestService;

    public TestDataSeeder(CreateBookModelService bookModelService, AddBookToStockService bookUnitService, CreateBookRequestService requestService) {
        //this.customerService = customerService;
        this.bookModelService = bookModelService;
        this.bookUnitService = bookUnitService;
        this.requestService = requestService;
    }

    @PostConstruct
    public void seed() {
        bookModelService.execute(new CreateBookModelInput("978-5-38-924797-0", "Сёгун", "Джеймс Клавелл", BigDecimal.valueOf(480)));
        bookModelService.execute(new CreateBookModelInput("978-5-04-122102-7", "Над пропостью во ржи", "Сэлинджер Джером Дэвид", BigDecimal.valueOf(478)));
        bookModelService.execute(new CreateBookModelInput("978-5-17-119245-7", "Сто лет одиночества", "Маркес Габриэль Гарсиа", BigDecimal.valueOf(979)));
        bookModelService.execute(new CreateBookModelInput("978-5-38-919086-3", "Анна Каренина", "Толстой Лев Николаевич", BigDecimal.valueOf(341)));
        bookModelService.execute(new CreateBookModelInput("978-5-44-610772-8", "Чистая архитектура", "Роберт Мартин", BigDecimal.valueOf(865)));
        bookModelService.execute(new CreateBookModelInput("978-5-17-175640-6", "Сага о Форсайтах", "Голсуорси Джон", BigDecimal.valueOf(519)));

        bookUnitService.execute(new AddBookToStockInput("978-5-38-919086-3"));
        bookUnitService.execute(new AddBookToStockInput("978-5-17-119245-7"));
        bookUnitService.execute(new AddBookToStockInput("978-5-17-175640-6"));
        bookUnitService.execute(new AddBookToStockInput("978-5-17-175640-6"));
        bookUnitService.execute(new AddBookToStockInput("978-5-17-175640-6"));
        bookUnitService.execute(new AddBookToStockInput("978-5-38-924797-0"));
        bookUnitService.execute(new AddBookToStockInput("978-5-38-924797-0"));

        requestService.execute(new CreateBookRequestInput("978-5-38-919086-3", 1L));
        requestService.execute(new CreateBookRequestInput("978-5-44-610772-8", 1L));
    }
}
