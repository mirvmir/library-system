package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.order.OrderItem;
import io.github.mirvmir.domain.entities.order.OrderStatus;
import io.github.mirvmir.exception.business.InvalidPathException;
import io.github.mirvmir.exception.business.OrderImportException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultImportOrderCsvService;
import io.github.mirvmir.useCases.services.inputs.ImportOrderCsvInput;
import io.github.mirvmir.useCases.services.outputs.ImportOrderOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultImportOrderCsvServiceTest {

    private OrderRepository orderRepo;
    private UserRepository customerRepo;
    private Config config;

    private DefaultImportOrderCsvService service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        orderRepo = mock(OrderRepository.class);
        customerRepo = mock(UserRepository.class);
        config = mock(Config.class);

        service = new DefaultImportOrderCsvService(orderRepo, customerRepo, config);
    }

    @Test
    void execute_shouldImportOrdersAndSaveAll_whenValidCsvPathProvided() throws IOException {
        Path csvFile = tempDir.resolve("orders.csv");
        Files.writeString(csvFile, """
                orderId,customerId,status,bookIsbn,bookId,completionDate
                1,10,NEW,111,1,null
                1,10,NEW,222,2,null
                2,20,COMPLETED,333,3,2024-04-04T00:00:00
                """);

        ImportOrderCsvInput input = new ImportOrderCsvInput(csvFile.toString());

        when(customerRepo.existUser(10L)).thenReturn(true);
        when(customerRepo.existUser(20L)).thenReturn(true);

        ImportOrderOutput result = service.execute(input);

        assertNotNull(result);

        verify(customerRepo).existUser(10L);
        verify(customerRepo).existUser(20L);

        verify(orderRepo).saveAll(argThat(orders -> {
            if (orders.size() != 2) {
                return false;
            }

            boolean hasOrderForCustomer10 = orders.stream().anyMatch(order ->
                    order.getCustomerId().equals(10L)
                            && order.getStatus() == OrderStatus.CREATED
                            && order.getCompletionAt() == null
                            && 0 == order.getTotalPrice().compareTo(BigDecimal.ZERO)
                            && 2 == order.getItems().size()
                            && order.getItems().stream().map(OrderItem::getBookId).toList().containsAll(List.of(1L, 2L))
                            && order.getItems().stream().map(OrderItem::getBookIsbn).toList().containsAll(List.of("111", "222"))
            );

            boolean hasOrderForCustomer20 = orders.stream().anyMatch(order ->
                    order.getCustomerId().equals(20L)
                            && order.getStatus() == OrderStatus.COMPLETED
                            && LocalDateTime.parse("2024-04-04T00:00:00").equals(order.getCompletionAt())
                            && 0 == order.getTotalPrice().compareTo(BigDecimal.ZERO)
                            && 1 == order.getItems().size()
                            && order.getItems().get(0).getBookId().equals(3L)
                            && order.getItems().get(0).getBookIsbn().equals("333")
            );

            return hasOrderForCustomer10 && hasOrderForCustomer20;
        }));
    }

    @Test
    void execute_shouldUsePathFromConfig_whenInputPathIsNull() throws IOException {
        Path csvFile = tempDir.resolve("orders-from-config.csv");
        Files.writeString(csvFile, """
                orderId,customerId,status,bookIsbn,bookId,completionDate
                1,10,NEW,111,1,null
                """);

        when(config.getImportCsvOrdersPath()).thenReturn(csvFile.toString());
        when(customerRepo.existUser(10L)).thenReturn(true);

        ImportOrderCsvInput input = new ImportOrderCsvInput(null);

        ImportOrderOutput result = service.execute(input);

        assertNotNull(result);

        verify(config).getImportCsvOrdersPath();
        verify(customerRepo).existUser(10L);
        verify(orderRepo).saveAll(argThat(orders ->
                1 == orders.size()
                        && orders.get(0).getCustomerId().equals(10L)
                        && OrderStatus.CREATED == orders.get(0).getStatus()
        ));
    }

    @Test
    void execute_shouldThrowInvalidPathException_whenPathDoesNotExist() {
        String invalidPath = tempDir.resolve("missing-orders.csv").toString();
        ImportOrderCsvInput input = new ImportOrderCsvInput(invalidPath);

        InvalidPathException ex = assertThrows(
                InvalidPathException.class,
                () -> service.execute(input)
        );

        assertEquals("Invalid path: " + invalidPath + ".", ex.getMessage());

        verifyNoInteractions(orderRepo);
        verifyNoInteractions(customerRepo);
        verifyNoInteractions(config);
    }

    @Test
    void execute_shouldIgnoreEmptyLines() throws IOException {
        Path csvFile = tempDir.resolve("orders-with-empty-lines.csv");
        Files.writeString(csvFile, """
                orderId,customerId,status,bookIsbn,bookId,completionDate
                
                1,10,NEW,111,1,null
                
                1,10,NEW,222,2,null
                
                """);

        when(customerRepo.existUser(10L)).thenReturn(true);

        ImportOrderCsvInput input = new ImportOrderCsvInput(csvFile.toString());

        ImportOrderOutput result = service.execute(input);

        assertNotNull(result);

        verify(customerRepo).existUser(10L);
        verify(orderRepo).saveAll(argThat(orders ->
                1 == orders.size()
                        && 2 == orders.get(0).getItems().size()
        ));
    }

    @Test
    void execute_shouldThrowOrderImportException_whenCsvContainsInvalidRow() throws IOException {
        Path csvFile = tempDir.resolve("invalid-row.csv");
        Files.writeString(csvFile, """
                orderId,customerId,status,bookIsbn,bookId,completionDate
                1,10,NEW,111,1,null
                broken,row
                """);

        ImportOrderCsvInput input = new ImportOrderCsvInput(csvFile.toString());

        OrderImportException ex = assertThrows(
                OrderImportException.class,
                () -> service.execute(input)
        );

        assertTrue(ex.getMessage().contains("Некорректная строка: broken,row"));

        verifyNoInteractions(orderRepo);
        verifyNoInteractions(customerRepo);
    }

    @Test
    void execute_shouldThrowOrderImportException_whenSameOrderHasDifferentCustomerIds() throws IOException {
        Path csvFile = tempDir.resolve("different-customers.csv");
        Files.writeString(csvFile, """
                orderId,customerId,status,bookIsbn,bookId,completionDate
                1,10,NEW,111,1,null
                1,20,NEW,222,2,null
                """);

        ImportOrderCsvInput input = new ImportOrderCsvInput(csvFile.toString());

        OrderImportException ex = assertThrows(
                OrderImportException.class,
                () -> service.execute(input)
        );

        assertTrue(ex.getMessage().contains("Заказ 1 имеет разные customerId: 10 и 20"));

        verifyNoInteractions(orderRepo);
        verifyNoInteractions(customerRepo);
    }

    @Test
    void execute_shouldThrowOrderImportException_whenSameOrderHasDifferentStatuses() throws IOException {
        Path csvFile = tempDir.resolve("different-statuses.csv");
        Files.writeString(csvFile, """
                orderId,customerId,status,bookIsbn,bookId,completionDate
                1,10,NEW,111,1,null
                1,10,COMPLETED,222,2,null
                """);

        ImportOrderCsvInput input = new ImportOrderCsvInput(csvFile.toString());

        OrderImportException ex = assertThrows(
                OrderImportException.class,
                () -> service.execute(input)
        );

        assertTrue(ex.getMessage().contains("Заказ 1 имеет разные статусы: NEW и COMPLETED"));

        verifyNoInteractions(orderRepo);
        verifyNoInteractions(customerRepo);
    }

    @Test
    void execute_shouldThrowOrderImportException_whenSameOrderHasDifferentCompletionDates() throws IOException {
        Path csvFile = tempDir.resolve("different-completion-dates.csv");
        Files.writeString(csvFile, """
                orderId,customerId,status,bookIsbn,bookId,completionDate
                1,10,COMPLETED,111,1,2024-04-04T00:00:00
                1,10,COMPLETED,222,2,2025-05-05T00:00:00
                """);

        ImportOrderCsvInput input = new ImportOrderCsvInput(csvFile.toString());

        OrderImportException ex = assertThrows(
                OrderImportException.class,
                () -> service.execute(input)
        );

        assertTrue(ex.getMessage()
                .contains("Заказ 1 имеет разные completionDate: 2024-04-04T00:00 и 2025-05-05T00:00")
        );

        verifyNoInteractions(orderRepo);
        verifyNoInteractions(customerRepo);
    }

    @Test
    void execute_shouldThrowOrderImportException_whenCustomerDoesNotExist() throws IOException {
        Path csvFile = tempDir.resolve("customer-not-found.csv");
        Files.writeString(csvFile, """
                orderId,customerId,status,bookIsbn,bookId,completionDate
                1,10,NEW,111,1,null
                """);

        when(customerRepo.existUser(10L)).thenReturn(false);

        ImportOrderCsvInput input = new ImportOrderCsvInput(csvFile.toString());

        OrderImportException ex = assertThrows(
                OrderImportException.class,
                () -> service.execute(input)
        );

        assertEquals("Покупатель с ID 10 не найден.", ex.getMessage());

        verify(customerRepo).existUser(10L);
        verify(orderRepo, never()).saveAll(any());
    }

    @Test
    void execute_shouldAllowNullBookId() throws IOException {
        Path csvFile = tempDir.resolve("null-book-id.csv");
        Files.writeString(csvFile, """
                orderId,customerId,status,bookIsbn,bookId,completionDate
                1,10,NEW,111,null,null
                """);

        when(customerRepo.existUser(10L)).thenReturn(true);

        ImportOrderCsvInput input = new ImportOrderCsvInput(csvFile.toString());

        ImportOrderOutput result = service.execute(input);

        assertNotNull(result);

        verify(orderRepo).saveAll(argThat(orders ->
                1 == orders.size()
                        && 1 == orders.get(0).getItems().size()
                        && orders.get(0).getItems().get(0).getBookId() == null
                        && "111".equals(orders.get(0).getItems().get(0).getBookIsbn())
        ));
    }

    @Test
    void execute_shouldThrowIllegalStateException_whenPathPointsToDirectory() throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("orders-dir"));
        ImportOrderCsvInput input = new ImportOrderCsvInput(directory.toString());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.execute(input)
        );

        assertEquals("Ошибка чтения файла.", ex.getMessage());

        verifyNoInteractions(orderRepo);
        verifyNoInteractions(customerRepo);
    }
}