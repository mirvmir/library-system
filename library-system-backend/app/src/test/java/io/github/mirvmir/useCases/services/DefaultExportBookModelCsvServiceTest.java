package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.InvalidPathException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultExportBookModelCsvService;
import io.github.mirvmir.useCases.services.inputs.ExportBookModelCsvInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DefaultExportBookModelCsvServiceTest {
    private BookModelRepository modelRepo;
    private Config config;

    private DefaultExportBookModelCsvService service;

    @BeforeEach
    void setUp() {
        modelRepo = mock(BookModelRepository.class);
        config = mock(Config.class);

        service = new DefaultExportBookModelCsvService(modelRepo, config);
    }

    @Test
    void execute_shouldExportBookModel_whenPathNotNull(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("books.csv");
        Files.createFile(file);
        String path = file.toString();
        ExportBookModelCsvInput input = new ExportBookModelCsvInput(path);

        List<BookModel> models = List.of(
                new BookModel(
                        "123",
                        "Преступление и наказание",
                        "Федор Михайлович Достоевский",
                        new BigDecimal("345"),
                        0,
                        2
                )
        );

        when(modelRepo.findAll()).thenReturn(models);

        service.execute(input);

        List<String> lines = Files.readAllLines(file);

        assertEquals("isbn,title,author,price,stockCount,requestCount", lines.get(0));
        assertEquals(
                "123,Преступление и наказание,Федор Михайлович Достоевский,345,0,2",
                lines.get(1)
        );
    }

    @Test
    void execute_shouldExportBookModel_whenPathNull(@TempDir Path tempDir)
            throws IOException {
        Path file = tempDir.resolve("books.csv");
        Files.createFile(file);
        String path = file.toString();
        ExportBookModelCsvInput input = new ExportBookModelCsvInput(null);

        when(config.getExportCsvModelsPath())
                .thenReturn(path);

        List<BookModel> models = List.of(
                new BookModel(
                        "123",
                        "Преступление и наказание",
                        "Федор Михайлович Достоевский",
                        new BigDecimal("345"),
                        0,
                        2
                )
        );

        when(modelRepo.findAll()).thenReturn(models);

        service.execute(input);

        List<String> lines = Files.readAllLines(file);

        assertEquals("isbn,title,author,price,stockCount,requestCount", lines.get(0));
        assertEquals(
                "123,Преступление и наказание,Федор Михайлович Достоевский,345,0,2",
                lines.get(1)
        );
    }

    @Test
    void execute_shouldNotExportBookModel_shouldThrowException_whenInvalidPath() {
        String invalidPath = "/not/existing/path/file.csv";

        ExportBookModelCsvInput input = new ExportBookModelCsvInput(invalidPath);

        assertThrows(
                InvalidPathException.class,
                () -> service.execute(input)
        );
    }
}
