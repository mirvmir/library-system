package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.controllers.web.requests.LoginRq;
import io.github.mirvmir.controllers.web.requests.RegisterRq;
import io.github.mirvmir.useCases.services.outputs.RegisterOutput;
import io.github.mirvmir.useCases.services.outputs.TokenOutput;
import io.github.mirvmir.useCases.services.outputs.LoginOutput;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface AuthService {
    LoginOutput login(LoginRq request);
    TokenOutput generateToken(Long userId, Collection<? extends GrantedAuthority> authorities);
    RegisterOutput register(RegisterRq request);
}
