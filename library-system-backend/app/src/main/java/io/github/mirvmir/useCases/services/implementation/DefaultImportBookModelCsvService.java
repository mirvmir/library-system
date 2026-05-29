package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.BookImportException;
import io.github.mirvmir.exception.business.InvalidPathException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.services.interfaces.ImportBookModelCsvService;
import io.github.mirvmir.useCases.services.inputs.ImportBookModelCsvInput;
import io.github.mirvmir.useCases.services.outputs.ImportBookModelOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultImportBookModelCsvService implements ImportBookModelCsvService {

    private final BookModelRepository modelRepo;
    private final Config config;

    public DefaultImportBookModelCsvService(BookModelRepository modelRepo,
                                            Config config) {
        this.modelRepo = modelRepo;
        this.config = config;
    }

    @Override
    @Transactional
    public ImportBookModelOutput execute(ImportBookModelCsvInput input) {

        List<BookModel> books = new ArrayList<>();

        String pathStr = null == input.path()
                ? config.getImportCsvModelsPath()
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
                    String isbn = parts[0];
                    String title = parts[1];
                    String author = parts[2];
                    BigDecimal price = new BigDecimal(parts[3]);

                    books.add(new BookModel(isbn, title, author, price, 0, 0));
                } catch (Exception ex) {
                    throw new BookImportException("\nНекорректная строка: " + line);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка чтения файла.");
        }

        for (BookModel imported : books) {
            BookModel existing;

            existing = modelRepo.findByIsbn(imported.getIsbn());

            if (null == existing) {
                modelRepo.save(imported);
                continue;
            }

            modelRepo.update(imported);
        }
        return new ImportBookModelOutput();
    }
}
