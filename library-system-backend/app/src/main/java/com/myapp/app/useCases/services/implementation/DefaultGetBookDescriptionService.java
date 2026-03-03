package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.exception.business.BookNotFoundException;
import com.myapp.app.useCases.adapter.repository.interfaces.BookModelRepository;
import com.myapp.app.useCases.services.inputs.BookDescriptionInput;
import com.myapp.app.useCases.services.interfaces.GetBookDescriptionService;
import com.myapp.app.useCases.services.outputs.BookDescriptionOutput;
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
