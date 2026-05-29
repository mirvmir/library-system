package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.order.Order;
import io.github.mirvmir.exception.business.InvalidPathException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
import io.github.mirvmir.useCases.services.interfaces.ExportOrderCsvService;
import io.github.mirvmir.useCases.services.inputs.ExportOrderCsvInput;
import io.github.mirvmir.useCases.services.outputs.ExportOrderOutput;
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
                            + order.getExpiresAt());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка записи файла.");
        }

        return new ExportOrderOutput();
    }
}
