package io.github.mirvmir.controllers.controller;

import io.github.mirvmir.config.TestConfig;
import io.github.mirvmir.config.WebConfig;
import io.github.mirvmir.exception.business.*;
import io.github.mirvmir.useCases.services.inputs.*;
import io.github.mirvmir.useCases.services.interfaces.*;
import io.github.mirvmir.useCases.services.outputs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        WebConfig.class,
        TestConfig.class
})
@WebAppConfiguration
class BookControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private GetBookService getBookService;
    @Autowired
    private GetBookDescriptionService getBookDescriptionService;
    @Autowired
    private CreateBookModelService createBookModelService;
    @Autowired
    private AddBookToStockService addBookToStockService;
    @Autowired
    private WriteOffBookService writeOffBookService;
    @Autowired
    private ExportBookModelCsvService exportBookModelCsvService;
    @Autowired
    private ImportBookModelCsvService importBookModelCsvService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(
                getBookService,
                getBookDescriptionService,
                createBookModelService,
                addBookToStockService,
                writeOffBookService,
                exportBookModelCsvService,
                importBookModelCsvService
        );

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void getAll_shouldReturn200() throws Exception {
        GetBooksOutput output = new GetBooksOutput(List.of(
                new GetBookOutput(
                        "123",
                        "Преступление и наказание",
                        "Достоевский",
                        "321",
                        true))
        );

        when(getBookService.execute(any())).thenReturn(output);

       mockMvc.perform(
               get("/books")
                       .queryParam("type", "BOOK")
                       .queryParam("direction", "ASC")
                       .queryParam("field", "PRICE")
               )
               .andExpect(status().isOk())
               .andExpect(content().contentType(APPLICATION_JSON_VALUE))
               .andReturn();

        verify(getBookService).execute(argThat(request ->
                request != null
                        && request.type().equals("BOOK")
                        && request.direction().equals("ASC")
                        && request.field().equals("PRICE")
        ));
    }

    @Test
    void getAll_shouldReturn400() throws Exception {
        doThrow(new IncompatibleSortTypesException("Incompatible types for sorting: BOOK and DELIVERY_DATE."))
                .when(getBookService).execute(any(GetBookInput.class));

        mockMvc.perform( get("/books")
                        .queryParam("type", "BOOK")
                        .queryParam("direction", "ASC")
                        .queryParam("field", "DELIVERY_DATE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByIsbn_shouldReturn200() throws Exception {
        BookDescriptionOutput output = new BookDescriptionOutput(
                "123",
                "Преступление и наказание",
                "Достоевский",
                new BigDecimal(321),
                true
        );

        when(getBookDescriptionService.execute(any())).thenReturn(output);

        mockMvc.perform(get("/books/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        verify(getBookDescriptionService).execute(argThat(request ->
                request != null
                        && request.isbn().equals("123")
        ));
    }

    @Test
    void getByIsbn_shouldReturn404() throws Exception {
        doThrow(new BookNotFoundException("Book with ISBN 123 not found."))
                .when(getBookDescriptionService).execute(any(BookDescriptionInput.class));

        mockMvc.perform( get("/books/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn200() throws Exception {
        CreateBookModelOutput output = new CreateBookModelOutput("123");

        when(createBookModelService.execute(any())).thenReturn(output);

        mockMvc.perform(
                post("/books/stock")
                        .contentType(APPLICATION_JSON)
                        .content(""" 
                                {
                                  "isbn": "123",
                                  "title": "Преступление и наказание",
                                  "author": "Достоевский",
                                  "price": "1230"
                                }
                                """
                        )
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        verify(createBookModelService).execute(argThat(request ->
                request != null
                        && request.isbn().equals("123")
                        && request.title().equals("Преступление и наказание")
                        && request.author().equals("Достоевский")
                        && request.price().equals(new BigDecimal(1230))
        ));
    }

    @Test
    void create_shouldReturn400() throws Exception {
        doThrow(new DuplicateIsbnException("Book with ISBN 123 already exists."))
                .when(createBookModelService).execute(any(CreateBookModelInput.class));

        mockMvc.perform(post("/books/stock"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addBookToStock_shouldReturn200() throws Exception {
        AddBookToStockOutput output = new AddBookToStockOutput("123");

        when(addBookToStockService.execute(any())).thenReturn(output);

        mockMvc.perform(
                        post("/books/stock/123/add")
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        verify(addBookToStockService).execute(argThat(request ->
                request != null
                        && request.isbn().equals("123")
        ));
    }

    @Test
    void addBookToStock_shouldReturn404() throws Exception {
        doThrow(new BookNotFoundException("Book with ISBN 123 not found."))
                .when(addBookToStockService).execute(any(AddBookToStockInput.class));

        mockMvc.perform(post("/books/stock/123/add"))
                .andExpect(status().isNotFound());
    }

    @Test
    void writeOffBook_shouldReturn200() throws Exception {
        WriteOffBookOutput output = new WriteOffBookOutput("123");

        when(writeOffBookService.execute(any())).thenReturn(output);

        mockMvc.perform(
                        post("/books/stock/123/writeoff")
                                .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        verify(writeOffBookService).execute(argThat(request ->
                request != null
                        && request.isbn().equals("123")
        ));
    }

    @Test
    void writeOffBook_shouldReturn404() throws Exception {
        doThrow(new BookNotFoundException("Book with ISBN 123 not found."))
                .when(writeOffBookService).execute(any(WriteOffBookInput.class));

        mockMvc.perform(post("/books/stock/123/writeoff"))
                .andExpect(status().isNotFound());
    }

    @Test
    void exportCsv_shouldReturn200() throws Exception {
        ExportBookModelOutput output = new ExportBookModelOutput();

        when(exportBookModelCsvService.execute(any(ExportBookModelCsvInput.class)))
                .thenReturn(output);

        mockMvc.perform(get("/books/stock/export")
                        .param("path", "/tmp/books.csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(exportBookModelCsvService).execute(argThat(input ->
                input != null &&
                        input.path().equals("/tmp/books.csv")
        ));
    }

    @Test
    void exportCsv_shouldReturn400_whenPathInvalid() throws Exception {
        when(exportBookModelCsvService.execute(any(ExportBookModelCsvInput.class)))
                .thenThrow(new InvalidPathException("Invalid path: /tmp/books.csv."));

        mockMvc.perform(get("/books/stock/export")
                        .param("path", "/tmp/books.csv"))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void exportCsv_shouldReturn500_whenWriteFailed() throws Exception {
//        when(exportBookModelCsvService.execute(any(ExportBookModelCsvInput.class)))
//                .thenThrow(new IllegalStateException("Ошибка записи файла."));
//
//        mockMvc.perform(get("/books/stock/export")
//                        .param("path", "/tmp/books.csv"))
//                .andExpect(status().isInternalServerError());
//    }

    @Test
    void importCsv_shouldReturn200() throws Exception {
        ImportBookModelOutput output = new ImportBookModelOutput();

        when(importBookModelCsvService.execute(any(ImportBookModelCsvInput.class)))
                .thenReturn(output);

        mockMvc.perform(post("/books/stock/import")
                        .param("path", "/tmp/books.csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON));

        verify(importBookModelCsvService).execute(argThat(input ->
                input != null &&
                        input.path().equals("/tmp/books.csv")
        ));
    }

    @Test
    void importCsv_shouldReturn400_whenPathInvalid() throws Exception {
        when(importBookModelCsvService.execute(any(ImportBookModelCsvInput.class)))
                .thenThrow(new InvalidPathException("Invalid path: /tmp/books.csv."));

        mockMvc.perform(post("/books/stock/import")
                        .param("path", "/tmp/books.csv"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void importCsv_shouldReturn400_whenCsvLineInvalid() throws Exception {
        when(importBookModelCsvService.execute(any(ImportBookModelCsvInput.class)))
                .thenThrow(new BookImportException("Некорректная строка: bad,line"));

        mockMvc.perform(post("/books/stock/import")
                        .param("path", "/tmp/books.csv"))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void importCsv_shouldReturn500_whenReadFailed() throws Exception {
//        when(importBookModelCsvService.execute(any(ImportBookModelCsvInput.class)))
//                .thenThrow(new IllegalStateException("Ошибка чтения файла."));
//
//        mockMvc.perform(post("/books/stock/import")
//                        .param("path", "/tmp/books.csv"))
//                .andExpect(status().isInternalServerError());
//    }
}
