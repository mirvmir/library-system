package io.github.mirvmir.useCases.services.outputs;

import org.springframework.security.core.Authentication;

public record RegisterOutput(Authentication authentication) {
}
