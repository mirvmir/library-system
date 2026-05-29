package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import io.github.mirvmir.useCases.services.inputs.CreateAdminInput;
import io.github.mirvmir.useCases.services.interfaces.CreateAdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DefaultCreateAdminService implements CreateAdminService {
    private final UserRepository userRepo;

    public DefaultCreateAdminService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public void execute(CreateAdminInput input) {
        User user = userRepo.findById(input.userId());

        if (null == user) {
            throw new EntityNotFoundException("User with ID " + input.userId() + " not found.");
        }

        user.setAdmin();
        userRepo.save(user);
    }
}
