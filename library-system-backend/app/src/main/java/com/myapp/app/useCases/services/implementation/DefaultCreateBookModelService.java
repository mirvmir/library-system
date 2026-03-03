package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.exception.business.DuplicateIsbnException;
import com.myapp.app.useCases.adapter.repository.interfaces.BookModelRepository;
import com.myapp.app.useCases.services.interfaces.CreateBookModelService;
import com.myapp.app.useCases.services.inputs.CreateBookModelInput;
import com.myapp.app.useCases.services.outputs.CreateBookModelOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultCreateBookModelService implements CreateBookModelService {

    private final BookModelRepository modelRepo;

    public DefaultCreateBookModelService(BookModelRepository modelRepo) {
        this.modelRepo = modelRepo;
    }

    @Override
    @Transactional
    public CreateBookModelOutput execute(CreateBookModelInput input) {

        BookModel newBook;

        try {
            newBook = modelRepo.save(
                BookModel.createNew(
                        input.isbn(),
                        input.title(),
                        input.author(),
                        input.price()
                )
        );
        } catch (Exception e) {
            throw new DuplicateIsbnException("Book with ISBN " + input.isbn()
                    + " already exists.");
        }

        return new CreateBookModelOutput(newBook.getIsbn());
    }
}
