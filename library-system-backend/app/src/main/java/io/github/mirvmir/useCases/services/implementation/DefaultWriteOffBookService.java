package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.bookUnit.BookUnit;
import io.github.mirvmir.exception.business.BookNotFoundException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookUnitRepository;
import io.github.mirvmir.useCases.services.interfaces.WriteOffBookService;
import io.github.mirvmir.useCases.services.inputs.WriteOffBookInput;
import io.github.mirvmir.useCases.services.outputs.WriteOffBookOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultWriteOffBookService implements WriteOffBookService {

    private final BookUnitRepository unitRepo;
    private final BookModelRepository modelRepo;

    public DefaultWriteOffBookService(BookUnitRepository unitRepo,
                                      BookModelRepository modelRepo) {
        this.unitRepo = unitRepo;
        this.modelRepo = modelRepo;
    }

    @Override
    @Transactional
    public WriteOffBookOutput execute(WriteOffBookInput input) {
        BookModel removeBook = modelRepo.findByIsbn(input.isbn());
        if (null == removeBook) {
            throw new BookNotFoundException("Book with ISBN " + input.isbn() + " not found.");
        }
        removeBook.writeOffBookUnit();
        modelRepo.update(removeBook);

        BookUnit bookUnit = unitRepo.findByIsbn(removeBook);
        bookUnit.writeOff();
        unitRepo.update(bookUnit);

        return new WriteOffBookOutput(input.isbn());
    }
}
