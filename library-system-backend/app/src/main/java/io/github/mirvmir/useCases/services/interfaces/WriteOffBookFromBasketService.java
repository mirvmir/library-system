package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.IsbnInput;

public interface WriteOffBookFromBasketService {
    void execute(IsbnInput input);
}
