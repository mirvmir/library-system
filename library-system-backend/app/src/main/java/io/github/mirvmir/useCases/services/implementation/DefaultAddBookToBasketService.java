package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.bookModel.BookModel;
import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.exception.UnauthorizedException;
import io.github.mirvmir.exception.business.BookNotFoundException;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.BasketEntity;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BasketRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.BookModelRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import io.github.mirvmir.useCases.services.inputs.IsbnInput;
import io.github.mirvmir.useCases.services.interfaces.AddBookToBasketService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultAddBookToBasketService implements AddBookToBasketService {

    private final BookModelRepository modelRepo;
    private final BasketRepository basketRepo;
    private final UserRepository userRepo;

    public DefaultAddBookToBasketService(BookModelRepository modelRepo,
                                         BasketRepository basketRepo,
                                         UserRepository userRepo) {
        this.modelRepo = modelRepo;
        this.basketRepo = basketRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public void execute(IsbnInput input) {
        BookModel bookForBasket = modelRepo.findByIsbn(input.isbn());
        if (bookForBasket == null) {
            throw new BookNotFoundException("Book with ISBN " + input.isbn() + " not found.");
        }

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

        User user = userRepo.findById(currentUserId);
        if (user == null) {
            throw new IllegalStateException("User with ID " + currentUserId + " not found.");
        }

        BasketEntity basket = basketRepo.findByUserId(currentUserId);

        if (basket == null) {
            basket = BasketEntity.createNewBasket(user);
        }

        basket.add(bookForBasket);
    }
}
