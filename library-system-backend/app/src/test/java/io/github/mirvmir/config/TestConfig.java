package io.github.mirvmir.config;

import io.github.mirvmir.useCases.services.interfaces.*;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    // AnalyticsController
    @Bean
    public CalculateTotalEarningsByPeriodService calculateTotalEarningsByPeriodService() {
        return Mockito.mock(CalculateTotalEarningsByPeriodService.class);
    }

    @Bean
    public GetCompletedOrdersCountByPeriodService getCompletedOrdersCountByPeriodService() {
        return Mockito.mock(GetCompletedOrdersCountByPeriodService.class);
    }

    @Bean
    public CreateAdminService createAdminService() {
        return Mockito.mock(CreateAdminService.class);
    }

    // AuthController
    @Bean
    public RefreshService refreshService() {
        return Mockito.mock(RefreshService.class);
    }

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }

    // BookController
    @Bean
    public GetBookService getBookService() {
        return Mockito.mock(GetBookService.class);
    }

    @Bean
    public GetBookDescriptionService getBookDescriptionService() {
        return Mockito.mock(GetBookDescriptionService.class);
    }

    @Bean
    public CreateBookModelService createBookModelService() {
        return Mockito.mock(CreateBookModelService.class);
    }

    @Bean
    public AddBookToStockService addBookToStockService() {
        return Mockito.mock(AddBookToStockService.class);
    }

    @Bean
    public WriteOffBookService writeOffBookService() {
        return Mockito.mock(WriteOffBookService.class);
    }

    @Bean
    public ExportBookModelCsvService exportBookModelCsvService() {
        return Mockito.mock(ExportBookModelCsvService.class);
    }

    @Bean
    public ImportBookModelCsvService importBookModelCsvService() {
        return Mockito.mock(ImportBookModelCsvService.class);
    }

    // BookRequestController
    @Bean
    public GetBookRequestService getBookRequestService() {
        return Mockito.mock(GetBookRequestService.class);
    }

    // OrderController
    @Bean
    public GetOrderService getOrderService() {
        return Mockito.mock(GetOrderService.class);
    }

    @Bean
    public GetOrderDescriptionService getOrderDescriptionService() {
        return Mockito.mock(GetOrderDescriptionService.class);
    }

    @Bean
    public CreateOrderService createOrderService() {
        return Mockito.mock(CreateOrderService.class);
    }

    @Bean
    public CancelOrderService cancelOrderService() {
        return Mockito.mock(CancelOrderService.class);
    }

    @Bean
    public CompleteOrderService completeOrderService() {
        return Mockito.mock(CompleteOrderService.class);
    }

    @Bean
    public ExportOrderCsvService exportOrderCsvService() {
        return Mockito.mock(ExportOrderCsvService.class);
    }

    @Bean
    public ImportOrderCsvService importOrderCsvService() {
        return Mockito.mock(ImportOrderCsvService.class);
    }
}
