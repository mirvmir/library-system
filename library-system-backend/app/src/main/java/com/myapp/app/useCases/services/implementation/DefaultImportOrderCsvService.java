package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.order.Order;
import com.myapp.app.domain.entities.order.OrderItem;
import com.myapp.app.domain.entities.order.OrderStatus;
import com.myapp.app.exception.business.InvalidPathException;
import com.myapp.app.exception.business.OrderImportException;
import com.myapp.app.frameworks.Config;
import com.myapp.app.useCases.adapter.repository.interfaces.CustomerRepository;
import com.myapp.app.useCases.adapter.repository.interfaces.OrderRepository;
import com.myapp.app.useCases.services.interfaces.ImportOrderCsvService;
import com.myapp.app.useCases.services.inputs.ImportOrderCsvInput;
import com.myapp.app.useCases.services.outputs.ImportOrderOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultImportOrderCsvService implements ImportOrderCsvService {

    private final OrderRepository orderRepo;
    private final CustomerRepository customerRepo;
    private final Config config;

    public DefaultImportOrderCsvService(OrderRepository orderRepo,
                                        CustomerRepository customerRepo,
                                        Config config) {
        this.orderRepo = orderRepo;
        this.customerRepo = customerRepo;
        this.config = config;
    }

    @Override
    @Transactional
    public ImportOrderOutput execute(ImportOrderCsvInput input) {

        Map<Long, List<Long>> orderBookIds = new HashMap<>();
        Map<Long, List<String>> orderBookIsbn = new HashMap<>();
        Map<Long, Long> orderCustomers = new HashMap<>();
        Map<Long, OrderStatus> orderStatuses = new HashMap<>();
        Map<Long, LocalDateTime> orderCompletionDate = new HashMap<>();

        List<String> errors = new ArrayList<>();

        String pathStr = null == input.path()
                ? config.getImportCsvOrdersPath()
                : input.path();

        if (!Files.exists(Paths.get(pathStr))) {
            throw new InvalidPathException("Invalid path: " + pathStr + ".");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(pathStr))) {

            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                try {
                    Long orderId = Long.parseLong(parts[0].trim());
                    Long customerId = Long.parseLong(parts[1].trim());
                    OrderStatus status = OrderStatus.valueOf(parts[2].trim());
                    String bookIsbn = parts[3].trim();

                    String bookIdOrNull = parts[4].trim();
                    Long bookId = bookIdOrNull.equals("null") ? null : Long.parseLong(bookIdOrNull);

                    String dateOrNull = parts[5].trim();

                    LocalDateTime completionDate = dateOrNull.equals("null") ?
                            null : LocalDateTime.parse(dateOrNull);

                    Long existingCustomer = orderCustomers.get(orderId);
                    if (existingCustomer != null && !existingCustomer.equals(customerId)) {
                        errors.add("Заказ " + orderId
                                + " имеет разные customerId: "
                                + existingCustomer + " и " + customerId);
                        continue;
                    }
                    orderCustomers.put(orderId, customerId);

                    OrderStatus existingStatus = orderStatuses.get(orderId);
                    if (existingStatus != null && existingStatus != status) {
                        errors.add("Заказ " + orderId
                                + " имеет разные статусы: "
                                + existingStatus + " и " + status);
                        continue;
                    }
                    orderStatuses.put(orderId, status);

                    LocalDateTime existingCompletionDate = orderCompletionDate.get(orderId);
                    if (existingCompletionDate != null
                            && !existingCompletionDate.equals(completionDate)) {
                        errors.add("Заказ " + orderId
                                + " имеет разные completionDate: "
                                + existingCompletionDate + " и " + completionDate);
                        continue;
                    }
                    orderCompletionDate.put(orderId, completionDate);

                    orderBookIsbn
                            .computeIfAbsent(orderId, id -> new ArrayList<>())
                            .add(bookIsbn);

                    orderBookIds
                            .computeIfAbsent(orderId, id -> new ArrayList<>())
                            .add(bookId);
                } catch (Exception ex) {
                    errors.add("Некорректная строка: " + line);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка чтения файла.");
        }

        if (!errors.isEmpty()) {
            throw new OrderImportException("\n" + String.join("\n", errors));
        }

        List<Order> importedOrders = new ArrayList<>();

        for (Long orderId : orderBookIds.keySet()) {
            List<OrderItem> items = new ArrayList<>();
            for (int i = 0; i < orderBookIds.get(orderId).size(); i++) {
                items.add(new OrderItem(
                        orderBookIds.get(orderId).get(i),
                        orderBookIsbn.get(orderId).get(i))
                );
            }
            importedOrders.add(
                    new Order(
                            orderCustomers.get(orderId),
                            items,
                            orderStatuses.get(orderId),
                            LocalDateTime.now(),
                            BigDecimal.ZERO,
                            orderCompletionDate.get(orderId)
                    )
            );
        }

        for (Order importedOrder : importedOrders) {
            if (!customerRepo.existCustomer(importedOrder.getCustomerId()))
                throw new OrderImportException("Покупатель с ID " + importedOrder.getCustomerId()
                        + " не найден.");
        }

        orderRepo.saveAll(importedOrders);

        return new ImportOrderOutput();
    }
}
