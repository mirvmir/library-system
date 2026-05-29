package io.github.mirvmir.controllers.web.controller;

import io.github.mirvmir.controllers.web.requests.BookModelRq;
import io.github.mirvmir.controllers.web.requests.GetBookRq;
import io.github.mirvmir.controllers.web.requests.PathRq;
import io.github.mirvmir.useCases.services.inputs.*;
import io.github.mirvmir.useCases.services.interfaces.*;
import io.github.mirvmir.useCases.services.outputs.AddBookToStockOutput;
import io.github.mirvmir.useCases.services.outputs.BookDescriptionOutput;
import io.github.mirvmir.useCases.services.outputs.CreateBookModelOutput;
import io.github.mirvmir.useCases.services.outputs.ExportBookModelOutput;
import io.github.mirvmir.useCases.services.outputs.GetBooksOutput;
import io.github.mirvmir.useCases.services.outputs.ImportBookModelOutput;
import io.github.mirvmir.useCases.services.outputs.WriteOffBookOutput;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final GetBasketService getBasketService;
    private final AddBookToBasketService addBookToBasketService;
    private final WriteOffBookFromBasketService writeOffBookFromBasketService;

    public BookController(GetBookService getBookService,
                          GetBookDescriptionService getBookDescriptionService,
                          CreateBookModelService createBookModelService,
                          AddBookToStockService addBookToStockService,
                          WriteOffBookService writeOffBookService,
                          ExportBookModelCsvService exportBookModelCsvService,
                          ImportBookModelCsvService importBookModelCsvService,
                          GetBasketService getBasketService,
                          AddBookToBasketService addBookToBasketService,
                          WriteOffBookFromBasketService writeOffBookFromBasketService) {
        this.getBookService = getBookService;
        this.getBookDescriptionService = getBookDescriptionService;
        this.createBookModelService = createBookModelService;
        this.addBookToStockService = addBookToStockService;
        this.writeOffBookService = writeOffBookService;
        this.exportBookModelCsvService = exportBookModelCsvService;
        this.importBookModelCsvService = importBookModelCsvService;
        this.getBasketService = getBasketService;
        this.addBookToBasketService = addBookToBasketService;
        this.writeOffBookFromBasketService = writeOffBookFromBasketService;
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
    public BookDescriptionOutput getById(@PathVariable("isbn") String isbn) {
        return getBookDescriptionService.execute(new BookDescriptionInput(isbn));
    }

    @PostMapping("/stock")
    @PreAuthorize("hasRole('ADMIN')")
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

    @PostMapping("/stock/{isbn}/add")
    @PreAuthorize("hasRole('ADMIN')")
    public AddBookToStockOutput addToStock(@PathVariable("isbn") String isbn) {
        return addBookToStockService.execute(
                new AddBookToStockInput(isbn)
        );
    }

    @PostMapping("/basket/{isbn}/add")
    public void addToBasket(@PathVariable("isbn") String isbn) {
        addBookToBasketService.execute(
                new IsbnInput(isbn)
        );
    }

    @PostMapping("/basket/{isbn}/writeOff")
    public void writeOffFromBasket(@PathVariable("isbn") String isbn) {
        writeOffBookFromBasketService.execute(
                new IsbnInput(isbn)
        );
    }

    @PostMapping("/basket")
    @GetMapping
    public GetBooksOutput getBasket() {
        return getBasketService.execute();
    }

    @PostMapping("/stock/{isbn}/writeoff")
    @PreAuthorize("hasRole('ADMIN')")
    public WriteOffBookOutput writeOff(@PathVariable("isbn") String isbn) {
        return writeOffBookService.execute(
                new WriteOffBookInput(isbn)
        );
    }

    @GetMapping("/stock/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ExportBookModelOutput exportCsv(PathRq rq) {
        return exportBookModelCsvService.execute(
                new ExportBookModelCsvInput(rq.path())
        );
    }

    @PostMapping("/stock/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ImportBookModelOutput importCsv(PathRq rq) {
        return importBookModelCsvService.execute(
                new ImportBookModelCsvInput(rq.path())
        );
    }
}
