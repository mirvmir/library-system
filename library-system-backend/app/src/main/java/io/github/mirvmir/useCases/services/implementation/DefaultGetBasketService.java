package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.exception.UnauthorizedException;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.BasketEntity;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BasketRepository;
import io.github.mirvmir.useCases.services.inputs.IdInput;
import io.github.mirvmir.useCases.services.interfaces.GetBasketService;
import io.github.mirvmir.useCases.services.mapping.BookToGetBookRsMapper;
import io.github.mirvmir.useCases.services.outputs.GetBooksOutput;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultGetBasketService implements GetBasketService {

    private final BasketRepository basketRepo;

    public DefaultGetBasketService(BasketRepository basketRepo) {
        this.basketRepo = basketRepo;
    }

    @Override
    @Transactional
    public GetBooksOutput execute() {
        Long currentUserId = null;
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("User is not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            currentUserId = Long.valueOf(jwt.getSubject());
        }

        BasketEntity basket = basketRepo.findByUserId(currentUserId);

        List<BookModel> models = basket.getModels();

        return new GetBooksOutput(
                models.stream()
                        .map(BookToGetBookRsMapper::map)
                        .toList()
        );
    }
}
