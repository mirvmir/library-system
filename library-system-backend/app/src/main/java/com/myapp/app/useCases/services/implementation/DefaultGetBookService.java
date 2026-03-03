package com.myapp.app.useCases.services.implementation;

import com.myapp.app.domain.entities.bookModel.BookModel;
import com.myapp.app.exception.business.IncompatibleSortTypesException;
import com.myapp.app.frameworks.Config;
import com.myapp.app.useCases.adapter.repository.interfaces.BookModelRepository;
import com.myapp.app.useCases.adapter.repository.interfaces.BookUnitRepository;
import com.myapp.app.useCases.services.dto.StaleUnitRow;
import com.myapp.app.useCases.services.inputs.GetBookInput;
import com.myapp.app.useCases.services.interfaces.GetBookService;
import com.myapp.app.useCases.services.mapping.BookToGetBookRsMapper;
import com.myapp.app.useCases.services.mapping.StaleUnitRowToGetBookRsMapper;
import com.myapp.app.useCases.services.outputs.GetBooksOutput;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class DefaultGetBookService implements GetBookService {

    private final BookModelRepository modelRepo;
    private final BookUnitRepository unitRepo;

    private final Config config;

    public DefaultGetBookService(BookModelRepository modelRepo,
                                 BookUnitRepository unitRepo,
                                 Config config) {
        this.modelRepo = modelRepo;
        this.unitRepo = unitRepo;
        this.config = config;
    }

    @Override
    @Transactional
    public GetBooksOutput execute(GetBookInput input) {
        if ("BOOK".equals(input.type()) && "TITLE".equals(input.field())) {
            List<BookModel> books;
            if ("ASC".equals(input.direction())) {
                books = modelRepo.findAll()
                        .stream()
                        .sorted(Comparator.comparing(BookModel::getTitle))
                        .toList();
            } else {
                books = modelRepo.findAll()
                        .stream()
                        .sorted(Comparator.comparing(BookModel::getTitle,
                                Comparator.reverseOrder()))
                        .toList();
            }
            return new GetBooksOutput(books.stream().map(BookToGetBookRsMapper::map)
                    .toList());
        }
        if ("BOOK".equals(input.type()) && "PRICE".equals(input.field())) {
            List<BookModel> books;
            if ("ASC".equals(input.direction())) {
                books = modelRepo.findAll()
                        .stream()
                        .sorted(Comparator.comparing(BookModel::getPrice))
                        .toList();
            } else {
                books = modelRepo.findAll()
                        .stream()
                        .sorted(Comparator.comparing(BookModel::getPrice,
                                Comparator.reverseOrder()))
                        .toList();
            }
            return new GetBooksOutput(books.stream().map(BookToGetBookRsMapper::map).toList());
        }
        if ("BOOK".equals(input.type()) && "AVAILABILITY".equals(input.field())) {
            List<BookModel> books;
            if ("ASC".equals(input.direction())) {
                books = modelRepo.findAll()
                        .stream()
                        .sorted(Comparator.comparing(BookModel::isAvailable))
                        .toList();
            } else {
                books = modelRepo.findAll()
                        .stream()
                        .sorted(Comparator.comparing(BookModel::isAvailable,
                                Comparator.reverseOrder()))
                        .toList();
            }
            return new GetBooksOutput(books.stream().map(BookToGetBookRsMapper::map)
                    .toList());
        }
        if ("STALE_BOOK".equals(input.type()) && "DELIVERY_DATE".equals(input.field())) {
            int months = config.getStaleBookMonths();
            LocalDate staleDate = LocalDate.now().minusMonths(months);

            List<StaleUnitRow> units = unitRepo.findStaleUnitsByDeliveryDate(
                    staleDate,
                    input.direction()
            );

            return new GetBooksOutput(units.stream().map(StaleUnitRowToGetBookRsMapper::map)
                    .toList());
        }
        if ("STALE_BOOK".equals(input.type()) && "PRICE".equals(input.field())) {
            int months = config.getStaleBookMonths();
            LocalDate staleDate = LocalDate.now().minusMonths(months);

            List<StaleUnitRow> units = unitRepo.findStaleUnitsByPrice(
                    staleDate,
                    input.direction()
            );

            return new GetBooksOutput(units.stream().map(StaleUnitRowToGetBookRsMapper::map)
                    .toList());
        }

        throw new IncompatibleSortTypesException("Incompatible types for sorting: "
                + input.type()
                + " and "
                + input.type() + ".");
    }
}
