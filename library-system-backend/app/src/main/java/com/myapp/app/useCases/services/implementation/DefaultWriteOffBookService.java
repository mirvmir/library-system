package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.domain.entities.bookUnit.BookUnit;
import com.myapp.app.exception.business.BookNotFoundException;
import com.myapp.app.useCases.adapter.repository.interfaces.BookModelRepository;
import com.myapp.app.useCases.adapter.repository.interfaces.BookUnitRepository;
import com.myapp.app.useCases.services.interfaces.WriteOffBookService;
import com.myapp.app.useCases.services.inputs.WriteOffBookInput;
import com.myapp.app.useCases.services.outputs.WriteOffBookOutput;
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
