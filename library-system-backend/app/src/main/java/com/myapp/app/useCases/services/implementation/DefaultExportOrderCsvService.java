package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.order.Order;
import com.myapp.app.exception.business.InvalidPathException;
import com.myapp.app.frameworks.Config;
import com.myapp.app.useCases.adapter.repository.interfaces.OrderRepository;
import com.myapp.app.useCases.services.interfaces.ExportOrderCsvService;
import com.myapp.app.useCases.services.inputs.ExportOrderCsvInput;
import com.myapp.app.useCases.services.outputs.ExportOrderOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DefaultExportOrderCsvService implements ExportOrderCsvService {

    private final OrderRepository orderRepo;
    private final Config config;

    public DefaultExportOrderCsvService(OrderRepository orderRepo,
                                        Config config) {
        this.orderRepo = orderRepo;
        this.config = config;
    }

    @Override
    @Transactional
    public ExportOrderOutput execute(ExportOrderCsvInput input) {

        String pathStr = null == input.path()
                ? config.getExportCsvOrdersPath()
                : input.path();
        Path path = Paths.get(pathStr);

        if (!Files.exists(path)) {
            throw new InvalidPathException("Invalid path: " + pathStr + ".");
        }

        List<Order> orders = orderRepo.findAll();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathStr))) {
            writer.write("id,customerId,status,bookIsbn,bookId,totalPrice,completionDate");
            writer.newLine();

            for (Order order : orders) {
                for (int i = 0; i < order.getItems().size(); i++) {

                    Long bookId = order.getItems().get(i).getBookId();
                    String isbn = order.getItems().get(i).getBookIsbn();

                    writer.write(order.getId() + ","
                            + order.getCustomerId() + ","
                            + order.getStatus() + ","
                            + isbn + ","
                            + bookId + ","
                            + order.getTotalPrice() + ","
                            + order.getCompletionDate());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка записи файла.");
        }

        return new ExportOrderOutput();
    }
}
