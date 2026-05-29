package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.domain.entities.order.OrderItem;
import io.github.mirvmir.domain.entities.order.OrderStatus;
import io.github.mirvmir.exception.business.InvalidPathException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultExportOrderCsvService;
import io.github.mirvmir.useCases.services.inputs.ExportOrderCsvInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultExportOrderCsvServiceTest {
    private OrderRepository orderRepo;
    private Config config;

    private DefaultExportOrderCsvService service;

    @BeforeEach
    void setUp() {
        orderRepo = mock(OrderRepository.class);
        config = mock(Config.class);

        service = new DefaultExportOrderCsvService(orderRepo, config);
    }

//    @Test
//    void execute_shouldExportOrder_whenPathNotNull(@TempDir Path tempDir) throws IOException {
//        Path file = tempDir.resolve("orders.csv");
//        Files.createFile(file);
//        String path = file.toString();
//        ExportOrderCsvInput input = new ExportOrderCsvInput(path);
//
//        List<OrderItem> orderItems = new ArrayList<>(List.of(
//                new OrderItem(2L, "123"),
//                new OrderItem(null, "321")
//        ));
//        Order orderForExport1 = new Order(
//                4L,
//                orderItems,
//                OrderStatus.CREATED,
//                LocalDateTime.now(),
//                new BigDecimal(1230),
//                null);
//        orderForExport1.setId(1L);
//        List<Order> orders = List.of(orderForExport1);
//
//        when(orderRepo.findAll()).thenReturn(orders);
//
//        service.execute(input);
//
//        List<String> lines = Files.readAllLines(file);
//
//        assertEquals("id,customerId,status,bookIsbn,bookId,totalPrice,completionDate", lines.get(0));
//        assertEquals(
//                "1,4,NEW,123,2,1230,null",
//                lines.get(1)
//        );
//        assertEquals(
//                "1,4,NEW,321,null,1230,null",
//                lines.get(2)
//        );
//    }
//
//    @Test
//    void execute_shouldExportOrder_whenPathNull(@TempDir Path tempDir)
//            throws IOException {
//        Path file = tempDir.resolve("orders.csv");
//        Files.createFile(file);
//        String path = file.toString();
//        ExportOrderCsvInput input = new ExportOrderCsvInput(null);
//
//        when(config.getExportCsvOrdersPath())
//                .thenReturn(path);
//
//        List<OrderItem> orderItems = new ArrayList<>(List.of(
//                new OrderItem(2L, "123"),
//                new OrderItem(null, "321")
//        ));
//        Order orderForExport1 = new Order(
//                4L,
//                orderItems,
//                OrderStatus.NEW,
//                LocalDateTime.now(),
//                new BigDecimal(1230),
//                null);
//        orderForExport1.setId(1L);
//        List<Order> orders = List.of(orderForExport1);
//
//        when(orderRepo.findAll()).thenReturn(orders);
//
//        service.execute(input);
//
//        List<String> lines = Files.readAllLines(file);
//
//        assertEquals("id,customerId,status,bookIsbn,bookId,totalPrice,completionDate", lines.get(0));
//        assertEquals(
//                "1,4,NEW,123,2,1230,null",
//                lines.get(1)
//        );
//        assertEquals(
//                "1,4,NEW,321,null,1230,null",
//                lines.get(2)
//        );
//    }

    @Test
    void execute_shouldNotExportOrder_shouldThrowException_whenInvalidPath() {
        String invalidPath = "/not/existing/path/file.csv";

        ExportOrderCsvInput input = new ExportOrderCsvInput(invalidPath);

        assertThrows(
                InvalidPathException.class,
                () -> service.execute(input)
        );
    }
}
