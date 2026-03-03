package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.domain.entities.bookUnit.BookUnit;
import com.myapp.app.exception.business.BookNotFoundException;
import com.myapp.app.frameworks.Config;
import com.myapp.app.useCases.adapter.repository.interfaces.BookModelRepository;
import com.myapp.app.useCases.adapter.repository.interfaces.BookRequestRepository;
import com.myapp.app.useCases.adapter.repository.interfaces.BookUnitRepository;
import com.myapp.app.useCases.services.outputs.AddBookToStockOutput;
import com.myapp.app.useCases.services.interfaces.AddBookToStockService;
import com.myapp.app.useCases.services.inputs.AddBookToStockInput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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

        unitRepo.save(BookUnit.createNew(input.isbn(), LocalDate.now()));

        BookModel newBook = modelRepo.findByIsbn(input.isbn());
        if (null == newBook) {
            throw new BookNotFoundException("Book with ISBN " + input.isbn() + " not found.");
        }
        newBook.addBookUnit();

        if (config.isAutoCompleteRequests()) {
            newBook.removeBookRequest();
            requestRepo.deleteByIsbn(input.isbn());
        }

        modelRepo.update(newBook);

        return new AddBookToStockOutput(input.isbn());
    }
}
