package io.github.mirvmir.useCases.services.outputs;

import org.springframework.security.core.Authentication;

public record LoginOutput(Authentication authentication) {
}
