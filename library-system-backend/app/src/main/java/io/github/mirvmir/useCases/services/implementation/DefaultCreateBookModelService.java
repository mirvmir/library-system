package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.DuplicateIsbnException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.services.interfaces.CreateBookModelService;
import io.github.mirvmir.useCases.services.inputs.CreateBookModelInput;
import io.github.mirvmir.useCases.services.outputs.CreateBookModelOutput;
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
