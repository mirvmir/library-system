package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.request.BookRequest;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookRequestRepository;
import io.github.mirvmir.useCases.services.interfaces.CreateBookRequestService;
import io.github.mirvmir.useCases.services.inputs.CreateBookRequestInput;
import io.github.mirvmir.useCases.services.outputs.CreateBookRequestOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultCreateBookRequestService implements CreateBookRequestService {

    private final BookRequestRepository requestRepo;
    private final BookModelRepository modelRepo;

    public DefaultCreateBookRequestService(BookRequestRepository requestRepo,
                                           BookModelRepository modelRepo) {
        this.requestRepo = requestRepo;
        this.modelRepo = modelRepo;
    }

    @Override
    @Transactional
    public CreateBookRequestOutput execute(CreateBookRequestInput input) {
        BookRequest newRequest = requestRepo.save(
                BookRequest.createNew(
                        input.isbn(),
                        input.customerId()
                )
        );

        BookModel newBook = modelRepo.findByIsbn(input.isbn());
        newBook.addBookRequest();
        modelRepo.update(newBook);

        return new CreateBookRequestOutput(newRequest.getId());
    }
}
