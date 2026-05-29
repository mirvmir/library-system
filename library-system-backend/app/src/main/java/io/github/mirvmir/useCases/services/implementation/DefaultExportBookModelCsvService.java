package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.InvalidPathException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.services.interfaces.ExportBookModelCsvService;
import io.github.mirvmir.useCases.services.inputs.ExportBookModelCsvInput;
import io.github.mirvmir.useCases.services.outputs.ExportBookModelOutput;
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
public class DefaultExportBookModelCsvService implements ExportBookModelCsvService {

    private final BookModelRepository modelRepo;
    private final Config config;

    public DefaultExportBookModelCsvService(BookModelRepository modelRepo,
                                            Config config) {
        this.modelRepo = modelRepo;
        this.config = config;
    }

    @Override
    @Transactional
    public ExportBookModelOutput execute(ExportBookModelCsvInput input) {

        String pathStr = null == input.path()
                ? config.getExportCsvModelsPath()
                : input.path();
        Path path = Paths.get(pathStr);

        if (!Files.exists(path)) {
            throw new InvalidPathException("Invalid path: " + pathStr + ".");
        }

        List<BookModel> models = modelRepo.findAll();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathStr))) {
            writer.write("isbn,title,author,price,stockCount,requestCount");
            writer.newLine();

            for (BookModel model : models) {
                String line =
                        model.getIsbn() + ","
                                + escape(model.getTitle()) + ","
                                + escape(model.getAuthor()) + ","
                                + model.getPrice() + ","
                                + model.getStockCount() + ","
                                + model.getRequestCount();

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка записи файла.");
        }

        return new ExportBookModelOutput();
    }

    private String escape(String value) {
        if (value.contains(",")) {
            return "\"" + value + "\"";
        }
        return value;
    }
}
