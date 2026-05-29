package io.github.mirvmir.useCases.services.implementation;

import io.github.mirvmir.controllers.web.requests.LoginRq;
import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.controllers.web.requests.RegisterRq;
import io.github.mirvmir.exception.UnauthorizedException;
import io.github.mirvmir.useCases.services.outputs.RegisterOutput;
import io.github.mirvmir.useCases.services.outputs.TokenOutput;
import io.github.mirvmir.security.CustomUserDetails;
import io.github.mirvmir.security.JwtService;
import io.github.mirvmir.frameworks.dataAccess.hibernate.persistence.entities.RefreshToken;
import io.github.mirvmir.security.RefreshTokenHasher;
import io.github.mirvmir.useCases.adapter.repository.interfaces.RefreshTokenRepository;
import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
import io.github.mirvmir.useCases.services.interfaces.AuthService;
import io.github.mirvmir.useCases.services.outputs.LoginOutput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collection;
import java.util.UUID;

@Service
public class DefaultAuthService implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenHasher refreshTokenHasher;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepo;
    private final RefreshTokenRepository tokenRepo;


    @Value("${jwt.refresh-token.expiration}")
    private String expirationTime;

    public DefaultAuthService(AuthenticationManager authenticationManager,
                              JwtService jwtService,
                              RefreshTokenHasher refreshTokenHasher,
                              PasswordEncoder passwordEncoder,
                              UserRepository userRepo,
                              RefreshTokenRepository tokenRepo) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenHasher = refreshTokenHasher;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
    }

    @Override
    @Transactional
    public LoginOutput login(LoginRq request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return new LoginOutput(authentication);
    }

    @Override
    @Transactional
    public RegisterOutput register(RegisterRq request) {
        User newCustomer = User.createNewCustomer(
                passwordEncoder.encode(request.password()),
                request.email()
        );
        userRepo.save(newCustomer);

        UserDetails userDetails = new CustomUserDetails(newCustomer);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        return new RegisterOutput(authentication);
    }

    @Override
    @Transactional
    public TokenOutput generateToken(Long userId, Collection<? extends GrantedAuthority> authorities) {
        String accessToken = jwtService.generateToken(userId, authorities);

        String rawToken = UUID.randomUUID().toString();
        String tokenHash = refreshTokenHasher.hash(rawToken);

        RefreshToken refreshToken = new RefreshToken(
                userRepo.findById(userId),
                tokenHash,
                Duration.parse(expirationTime)
        );
        tokenRepo.save(refreshToken);
        // Подумать над ошибками возможными!!!!!! Потому что они если вылезут, то уже после flush...

        return new TokenOutput(accessToken, tokenHash);
    }
}
