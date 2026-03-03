package com.myapp.app.controllers.web.controller;

import com.myapp.app.controllers.requests.*;
import com.myapp.app.useCases.services.inputs.*;
import com.myapp.app.useCases.services.interfaces.*;
import com.myapp.app.useCases.services.outputs.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

    private final GetBookService getBookService;
    private final GetBookDescriptionService getBookDescriptionService;
    private final CreateBookModelService createBookModelService;
    private final AddBookToStockService addBookToStockService;
    private final WriteOffBookService writeOffBookService;
    private final ExportBookModelCsvService exportBookModelCsvService;
    private final ImportBookModelCsvService importBookModelCsvService;

    public BookController(GetBookService getBookService,
                          GetBookDescriptionService getBookDescriptionService,
                          CreateBookModelService createBookModelService,
                          AddBookToStockService addBookToStockService,
                          WriteOffBookService writeOffBookService,
                          ExportBookModelCsvService exportBookModelCsvService,
                          ImportBookModelCsvService importBookModelCsvService) {
        this.getBookService = getBookService;
        this.getBookDescriptionService = getBookDescriptionService;
        this.createBookModelService = createBookModelService;
        this.addBookToStockService = addBookToStockService;
        this.writeOffBookService = writeOffBookService;
        this.exportBookModelCsvService = exportBookModelCsvService;
        this.importBookModelCsvService = importBookModelCsvService;
    }

    @GetMapping
    public GetBooksOutput getAll(GetBookRq rq) {
        GetBookInput getRqDto = new GetBookInput(
                null == rq.type() ? null : rq.type().toString(),
                null == rq.direction() ? null : rq.direction().toString(),
                null == rq.field() ? null : rq.field().toString()
        );

        return getBookService.execute(getRqDto);
    }

    @GetMapping("/{isbn}")
    public BookDescriptionOutput getById(@PathVariable String isbn) {
        return getBookDescriptionService.execute(new BookDescriptionInput(isbn));
    }

    @PostMapping
    public CreateBookModelOutput create(@RequestBody BookModelRq rq) {
        return createBookModelService.execute(
                new CreateBookModelInput(
                        rq.isbn(),
                        rq.title(),
                        rq.author(),
                        rq.price()
                )
        );
    }

    @PostMapping("/{isbn}/stock")
    public AddBookToStockOutput addToStock(@PathVariable String isbn) {
        return addBookToStockService.execute(
                new AddBookToStockInput(isbn)
        );
    }

    @PostMapping("/{isbn}/writeoff")
    public WriteOffBookOutput writeOff(@PathVariable String isbn) {
        return writeOffBookService.execute(
                new WriteOffBookInput(isbn)
        );
    }

    @GetMapping("/export")
    public ExportBookModelOutput exportCsv(PathRq rq) {
        return exportBookModelCsvService.execute(
                new ExportBookModelCsvInput(rq.path())
        );
    }

    @PostMapping("/import")
    public ImportBookModelOutput importCsv(PathRq rq) {
        return importBookModelCsvService.execute(
                new ImportBookModelCsvInput(rq.path())
        );
    }
}
