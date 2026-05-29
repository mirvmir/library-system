package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.outputs.RefreshOutput;

public interface RefreshService {
    RefreshOutput execute(String rawToken);
}
