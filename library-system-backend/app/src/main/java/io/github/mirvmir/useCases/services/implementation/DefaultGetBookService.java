package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.business.IncompatibleSortTypesException;
import io.github.mirvmir.frameworks.Config;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookUnitRepository;
import io.github.mirvmir.useCases.services.dto.StaleUnitRow;
import io.github.mirvmir.useCases.services.inputs.GetBookInput;
import io.github.mirvmir.useCases.services.interfaces.GetBookService;
import io.github.mirvmir.useCases.services.mapping.BookToGetBookRsMapper;
import io.github.mirvmir.useCases.services.mapping.StaleUnitRowToGetBookRsMapper;
import io.github.mirvmir.useCases.services.outputs.GetBooksOutput;
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
