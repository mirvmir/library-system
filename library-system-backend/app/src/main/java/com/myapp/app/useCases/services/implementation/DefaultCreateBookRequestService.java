package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.domain.entities.request.BookRequest;
import com.myapp.app.useCases.adapter.repository.interfaces.BookModelRepository;
import com.myapp.app.useCases.adapter.repository.interfaces.BookRequestRepository;
import com.myapp.app.useCases.services.interfaces.CreateBookRequestService;
import com.myapp.app.useCases.services.inputs.CreateBookRequestInput;
import com.myapp.app.useCases.services.outputs.CreateBookRequestOutput;
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
