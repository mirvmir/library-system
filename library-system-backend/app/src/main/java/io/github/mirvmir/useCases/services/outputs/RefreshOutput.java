package io.github.mirvmir.useCases.services.outputs;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record RefreshOutput(Long userId, Collection<? extends GrantedAuthority> authorities) {
}
