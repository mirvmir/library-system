package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.BookNotFoundException;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.services.inputs.BookDescriptionInput;
import io.github.mirvmir.useCases.services.interfaces.GetBookDescriptionService;
import io.github.mirvmir.useCases.services.outputs.BookDescriptionOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultGetBookDescriptionService implements GetBookDescriptionService {

    private final BookModelRepository modelRepo;

    public DefaultGetBookDescriptionService(BookModelRepository modelRepo) {
        this.modelRepo = modelRepo;
    }

    @Override
    @Transactional
    public BookDescriptionOutput execute(BookDescriptionInput input) {
        BookModel book = modelRepo.findByIsbn(input.isbn());
        if (null == book) {
            throw new BookNotFoundException("Book with ISBN " + input.isbn() + " not found.");
        }

        return new BookDescriptionOutput(
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.isAvailable()
        );
    }
}
