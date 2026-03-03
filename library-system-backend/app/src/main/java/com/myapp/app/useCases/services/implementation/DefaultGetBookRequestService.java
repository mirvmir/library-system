package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.exception.business.IncompatibleSortTypesException;
import com.myapp.app.useCases.adapter.repository.interfaces.BookModelRepository;
import com.myapp.app.useCases.services.inputs.GetBookRequestInput;
import com.myapp.app.useCases.services.interfaces.GetBookRequestService;
import com.myapp.app.useCases.services.mapping.BookToGetBookRequestRsMapper;
import com.myapp.app.useCases.services.outputs.GetBookRequestsOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class DefaultGetBookRequestService implements GetBookRequestService {

    private final BookModelRepository modelRepo;

    public DefaultGetBookRequestService(BookModelRepository modelRepo) {
        this.modelRepo = modelRepo;
    }

    @Override
    @Transactional
    public GetBookRequestsOutput execute(GetBookRequestInput input) {
        if ("REQUEST".equals(input.type()) && "TITLE".equals(input.field())) {
            List<BookModel> books;
            if ("ASC".equals(input.direction())) {
                books = modelRepo.findAll()
                        .stream()
                        .filter(BookModel::isHaveRequest)
                        .sorted(Comparator.comparing(BookModel::getTitle))
                        .toList();
            } else {
                books = modelRepo.findAll()
                        .stream()
                        .filter(BookModel::isHaveRequest)
                        .sorted(Comparator.comparing(BookModel::getTitle,
                                Comparator.reverseOrder()))
                        .toList();
            }

            return new GetBookRequestsOutput(books.stream().map(BookToGetBookRequestRsMapper::map)
                    .toList());
        }
        if ("REQUEST".equals(input.type()) && "COUNT".equals(input.field())) {
            List<BookModel> books;
            if ("ASC".equals(input.direction())) {
                books = modelRepo.findAll()
                        .stream()
                        .filter(BookModel::isHaveRequest)
                        .sorted(Comparator.comparing(BookModel::getRequestCount))
                        .toList();
            } else {
                books = modelRepo.findAll()
                        .stream()
                        .filter(BookModel::isHaveRequest)
                        .sorted(Comparator.comparing(BookModel::getRequestCount,
                                Comparator.reverseOrder()))
                        .toList();
            }
            return new GetBookRequestsOutput(books.stream().map(BookToGetBookRequestRsMapper::map)
                    .toList());
        }

        throw new IncompatibleSortTypesException("Incompatible types for sorting: "
                + input.type()
                + " and "
                + input.type() + ".");
    }
}
