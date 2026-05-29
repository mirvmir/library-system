package io.github.mirvmir.controllers.web.controller;

import io.github.mirvmir.controllers.web.requests.LoginRq;
import io.github.mirvmir.controllers.web.responses.AccessTokenResponse;
import io.github.mirvmir.controllers.web.responses.RegisterResponse;
import io.github.mirvmir.controllers.web.responses.LoginResponse;
import io.github.mirvmir.controllers.web.requests.RegisterRq;
import io.github.mirvmir.security.CustomUserDetails;
import io.github.mirvmir.useCases.services.implementation.CreateBasketService;
import io.github.mirvmir.useCases.services.interfaces.AuthService;
import io.github.mirvmir.useCases.services.interfaces.RefreshService;
import io.github.mirvmir.useCases.services.outputs.RefreshOutput;
import io.github.mirvmir.useCases.services.outputs.RegisterOutput;
import io.github.mirvmir.useCases.services.outputs.LoginOutput;
import io.github.mirvmir.useCases.services.outputs.TokenOutput;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RefreshService refreshService;
    private final AuthService authService;
    private final CreateBasketService createBasketService;

    public AuthController(RefreshService refreshService,
                          AuthService authService,
                          CreateBasketService createBasketService) {
        this.refreshService = refreshService;
        this.authService = authService;
        this.createBasketService = createBasketService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRq request,
                               HttpServletResponse response) {
        LoginOutput output = authService.login(request);
        Authentication auth = output.authentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        TokenOutput tokens = authService.generateToken(user.getId(), auth.getAuthorities());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return new LoginResponse(tokens.accessToken());
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRq request,
                                  HttpServletResponse response) {
        RegisterOutput output = authService.register(request);
        Authentication auth = output.authentication();
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        TokenOutput tokens = authService.generateToken(user.getId(), auth.getAuthorities());

        createBasketService.execute();

        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return new RegisterResponse(tokens.accessToken());
    }

    @PostMapping("/refresh")
    public AccessTokenResponse refresh(
            @CookieValue(name = "refresh_token", required = false) String rawToken,
            HttpServletResponse response
    ) {
        RefreshOutput output = refreshService.execute(rawToken);

        TokenOutput tokens = authService.generateToken(output.userId(), output.authorities());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokens.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return new AccessTokenResponse(tokens.accessToken());
    }
}