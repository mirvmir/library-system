package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.bookUnit.BookUnit;
import io.github.mirvmir.exception.business.BookNotFoundException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookRequestRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookUnitRepository;
import io.github.mirvmir.useCases.services.outputs.AddBookToStockOutput;
import io.github.mirvmir.useCases.services.interfaces.AddBookToStockService;
import io.github.mirvmir.useCases.services.inputs.AddBookToStockInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultAddBookToStockService implements AddBookToStockService {

    private final Config config;
    private final BookUnitRepository unitRepo;
    private final BookModelRepository modelRepo;
    private final BookRequestRepository requestRepo;

    public DefaultAddBookToStockService(Config config,
                                        BookUnitRepository unitRepo,
                                        BookModelRepository modelRepo,
                                        BookRequestRepository requestRepo) {
        this.config = config;
        this.unitRepo = unitRepo;
        this.modelRepo = modelRepo;
        this.requestRepo = requestRepo;
    }

    @Override
    @Transactional
    public AddBookToStockOutput execute(AddBookToStockInput input) {

        BookModel newBook = modelRepo.findByIsbn(input.isbn());
        if (null == newBook) {
            throw new BookNotFoundException("Book with ISBN " + input.isbn() + " not found.");
        }

        unitRepo.save(BookUnit.createNew(input.isbn()));
        newBook.addBookUnit();

        if (config.isAutoCompleteRequests()) {
            newBook.removeBookRequest();
            requestRepo.deleteByIsbn(input.isbn());
        }

        modelRepo.update(newBook);

        return new AddBookToStockOutput(input.isbn());
    }
}
