package io.github.mirvmir.useCases.services;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.BookImportException;
import io.github.mirvmir.exception.business.InvalidPathException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.services.implementation.DefaultImportBookModelCsvService;
import io.github.mirvmir.useCases.services.inputs.ImportBookModelCsvInput;
import io.github.mirvmir.useCases.services.outputs.ImportBookModelOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultImportBookModelCsvServiceTest {
    private BookModelRepository modelRepo;
    private Config config;

    private DefaultImportBookModelCsvService service;

    @BeforeEach
    void setUp() {
        modelRepo = mock(BookModelRepository.class);
        config = mock(Config.class);

        service = new DefaultImportBookModelCsvService(modelRepo, config);
    }

    @TempDir
    Path tempDir;

    @Test
    void execute_shouldImportNewBooksAndSaveThem_whenValidCsvPathProvided() throws IOException {
        Path csvFile = tempDir.resolve("models.csv");
        Files.writeString(csvFile, """
                isbn,title,author,price
                111,Java Core,Smith,10.50
                222,Clean Code,Martin,20.00
                """);

        ImportBookModelCsvInput input = new ImportBookModelCsvInput(csvFile.toString());

        when(modelRepo.findByIsbn("111")).thenReturn(null);
        when(modelRepo.findByIsbn("222")).thenReturn(null);

        ImportBookModelOutput result = service.execute(input);

        assertNotNull(result);

        verify(modelRepo).findByIsbn("111");
        verify(modelRepo).findByIsbn("222");

        verify(modelRepo).save(argThat(book ->
                "111".equals(book.getIsbn())
                        && "Java Core".equals(book.getTitle())
                        && "Smith".equals(book.getAuthor())
                        && new BigDecimal("10.50").compareTo(book.getPrice()) == 0
        ));

        verify(modelRepo).save(argThat(book ->
                "222".equals(book.getIsbn())
                        && "Clean Code".equals(book.getTitle())
                        && "Martin".equals(book.getAuthor())
                        && new BigDecimal("20.00").compareTo(book.getPrice()) == 0
        ));

        verify(modelRepo, never()).update(any());
    }

    @Test
    void execute_shouldUsePathFromConfig_whenInputPathIsNull() throws IOException {
        Path csvFile = tempDir.resolve("models-from-config.csv");
        Files.writeString(csvFile, """
                isbn,title,author,price
                333,Refactoring,Fowler,30.00
                """);

        when(config.getImportCsvModelsPath()).thenReturn(csvFile.toString());
        when(modelRepo.findByIsbn("333")).thenReturn(null);

        ImportBookModelCsvInput input = new ImportBookModelCsvInput(null);

        ImportBookModelOutput result = service.execute(input);

        assertNotNull(result);

        verify(config).getImportCsvModelsPath();
        verify(modelRepo).findByIsbn("333");
        verify(modelRepo).save(argThat(book ->
                "333".equals(book.getIsbn())
                        && "Refactoring".equals(book.getTitle())
                        && "Fowler".equals(book.getAuthor())
                        && new BigDecimal("30.00").compareTo(book.getPrice()) == 0
        ));
        verify(modelRepo, never()).update(any());
    }

    @Test
    void execute_shouldThrowInvalidPathException_whenPathDoesNotExist() {
        String invalidPath = tempDir.resolve("missing-file.csv").toString();
        ImportBookModelCsvInput input = new ImportBookModelCsvInput(invalidPath);

        InvalidPathException ex = assertThrows(
                InvalidPathException.class,
                () -> service.execute(input)
        );

        assertEquals("Invalid path: " + invalidPath + ".", ex.getMessage());

        verifyNoInteractions(modelRepo);
        verifyNoInteractions(config);
    }

    @Test
    void execute_shouldThrowBookImportException_whenCsvContainsInvalidRow() throws IOException {
        Path csvFile = tempDir.resolve("invalid-row.csv");
        Files.writeString(csvFile, """
                isbn,title,author,price
                111,Java Core,Smith,10.50
                222,Broken Row
                """);

        ImportBookModelCsvInput input = new ImportBookModelCsvInput(csvFile.toString());

        BookImportException ex = assertThrows(
                BookImportException.class,
                () -> service.execute(input)
        );

        assertTrue(ex.getMessage().contains("Некорректная строка"));
        assertTrue(ex.getMessage().contains("222,Broken Row"));

        verifyNoInteractions(modelRepo);
    }

    @Test
    void execute_shouldThrowBookImportException_whenPriceIsInvalid() throws IOException {
        Path csvFile = tempDir.resolve("invalid-price.csv");
        Files.writeString(csvFile, """
                isbn,title,author,price
                111,Java Core,Smith,abc
                """);

        ImportBookModelCsvInput input = new ImportBookModelCsvInput(csvFile.toString());

        BookImportException ex = assertThrows(
                BookImportException.class,
                () -> service.execute(input)
        );

        assertTrue(ex.getMessage().contains("Некорректная строка"));
        assertTrue(ex.getMessage().contains("111,Java Core,Smith,abc"));

        verifyNoInteractions(modelRepo);
    }

    @Test
    void execute_shouldIgnoreEmptyLines() throws IOException {
        Path csvFile = tempDir.resolve("with-empty-lines.csv");
        Files.writeString(csvFile, """
                isbn,title,author,price
                
                111,Java Core,Smith,10.50
                
                222,Clean Code,Martin,20.00
                
                """);

        ImportBookModelCsvInput input = new ImportBookModelCsvInput(csvFile.toString());

        when(modelRepo.findByIsbn("111")).thenReturn(null);
        when(modelRepo.findByIsbn("222")).thenReturn(null);

        ImportBookModelOutput result = service.execute(input);

        assertNotNull(result);

        verify(modelRepo, times(2)).findByIsbn(anyString());
        verify(modelRepo, times(2)).save(any(BookModel.class));
        verify(modelRepo, never()).update(any());
    }

    @Test
    void execute_shouldUpdateBook_whenBookAlreadyExists() throws IOException {
        Path csvFile = tempDir.resolve("existing-book.csv");
        Files.writeString(csvFile, """
                isbn,title,author,price
                111,Java Core,Smith,10.50
                """);

        ImportBookModelCsvInput input = new ImportBookModelCsvInput(csvFile.toString());

        BookModel existing = new BookModel("111", "Old Title", "Old Author", new BigDecimal("5.00"), 0, 0);
        when(modelRepo.findByIsbn("111")).thenReturn(existing);

        ImportBookModelOutput result = service.execute(input);

        assertNotNull(result);

        verify(modelRepo).findByIsbn("111");
        verify(modelRepo, never()).save(any());
        verify(modelRepo).update(argThat(book ->
                "111".equals(book.getIsbn())
                        && "Java Core".equals(book.getTitle())
                        && "Smith".equals(book.getAuthor())
                        && new BigDecimal("10.50").compareTo(book.getPrice()) == 0
        ));
    }

    @Test
    void execute_shouldSaveNewBookAndUpdateExistingBook_whenCsvContainsBothTypes() throws IOException {
        Path csvFile = tempDir.resolve("mixed.csv");
        Files.writeString(csvFile, """
                isbn,title,author,price
                111,Java Core,Smith,10.50
                222,Clean Code,Martin,20.00
                """);

        ImportBookModelCsvInput input = new ImportBookModelCsvInput(csvFile.toString());

        when(modelRepo.findByIsbn("111")).thenReturn(null);
        when(modelRepo.findByIsbn("222"))
                .thenReturn(new BookModel("222", "Old", "Old", new BigDecimal("1.00"), 0, 0));

        ImportBookModelOutput result = service.execute(input);

        assertNotNull(result);

        verify(modelRepo).save(argThat(book -> "111".equals(book.getIsbn())));
        verify(modelRepo).update(argThat(book -> "222".equals(book.getIsbn())));
    }

    @Test
    void execute_shouldThrowIllegalStateException_whenPathPointsToDirectory() throws IOException {
        Path directory = Files.createDirectory(tempDir.resolve("dir"));
        ImportBookModelCsvInput input = new ImportBookModelCsvInput(directory.toString());

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.execute(input)
        );

        assertEquals("Ошибка чтения файла.", ex.getMessage());

        verifyNoInteractions(modelRepo);
    }
}