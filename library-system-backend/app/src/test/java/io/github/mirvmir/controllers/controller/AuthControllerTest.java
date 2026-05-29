package io.github.mirvmir.controllers.controller;

import io.github.mirvmir.config.TestConfig;
import io.github.mirvmir.config.WebConfig;
import io.github.mirvmir.controllers.web.requests.LoginRq;
import io.github.mirvmir.controllers.web.requests.RegisterRq;
import io.github.mirvmir.domain.entities.user.Role;
import io.github.mirvmir.domain.entities.user.User;
import io.github.mirvmir.security.CustomUserDetails;
import io.github.mirvmir.useCases.services.interfaces.AuthService;
import io.github.mirvmir.useCases.services.interfaces.RefreshService;
import io.github.mirvmir.useCases.services.outputs.LoginOutput;
import io.github.mirvmir.useCases.services.outputs.RefreshOutput;
import io.github.mirvmir.useCases.services.outputs.RegisterOutput;
import io.github.mirvmir.useCases.services.outputs.TokenOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        WebConfig.class,
        TestConfig.class
})
@WebAppConfiguration
public class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private RefreshService refreshService;
    @Autowired
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(
                refreshService,
                authService
        );

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void login_shouldReturn200() throws Exception {
        User user = mock(User.class);
        when(user.getId())
                .thenReturn(1L);
        when(user.getEmail())
                .thenReturn("toma@example.com");
        when(user.getRole())
                .thenReturn(Role.USER);
        when(user.getPasswordHash())
                .thenReturn("password_hash");
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        LoginOutput loginOutput = new LoginOutput(authentication);
        TokenOutput tokenOutput = new TokenOutput("access-token", "refresh-token");

        when(authService.login(any(LoginRq.class))).thenReturn(loginOutput);
        when(authService.generateToken(eq(1L), any())).thenReturn(tokenOutput);

        MvcResult mvcResult = mockMvc.perform(
                        post("/auth/login")
                                .contentType(APPLICATION_JSON).content("""
                                        {
                                          "email": "admin@example.com",
                                          "password": "12345"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Set-Cookie"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        String setCookie = mvcResult.getResponse().getHeader("Set-Cookie");

        assertTrue(jsonResponse.contains("access-token"));
        assertTrue(setCookie != null && setCookie.contains("refresh-token"));

        verify(authService).login(argThat(request ->
                request != null
                        && request.email().equals("admin@example.com")
                        && request.password().equals("12345")
        ));

        verify(authService).generateToken(eq(1L), any());
    }

//    @Test
//    void login_shouldReturn401() throws Exception {
//        doThrow(new UnauthorizedException("Invalid email or password"))
//                .when(authService).login(any(LoginRq.class));
//
//        mockMvc.perform(post("/auth/login"))
//                .andExpect(status().isUnauthorized());
//    }

    @Test
    void register_shouldReturn200() throws Exception {
        User user = mock(User.class);
        when(user.getId())
                .thenReturn(2L);
        when(user.getEmail())
                .thenReturn("toma@example.com");
        when(user.getRole())
                .thenReturn(Role.USER);
        when(user.getPasswordHash())
                .thenReturn("password_hash");
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        RegisterOutput registerOutput = new RegisterOutput(authentication);
        TokenOutput tokenOutput = new TokenOutput("new-access-token", "new-refresh-token");

        when(authService.register(any(RegisterRq.class))).thenReturn(registerOutput);
        when(authService.generateToken(eq(2L), any())).thenReturn(tokenOutput);

        MvcResult mvcResult = mockMvc.perform(
                        post("/auth/register")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                          "email": "toma@mail.com",
                                          "password": "12345"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Set-Cookie"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        String setCookie = mvcResult.getResponse().getHeader("Set-Cookie");

        assertTrue(jsonResponse.contains("new-access-token"));
        assertTrue(setCookie != null && setCookie.contains("new-refresh-token"));

        verify(authService).register(argThat(request ->
                request != null
                        && request.email().equals("toma@mail.com")
                        && request.password().equals("12345")
        ));

        verify(authService).generateToken(eq(2L), any());
    }

//    @Test
//    void register_shouldReturn409() throws Exception {
//        doThrow(new UserAlreadyExistsException("User already exists with email"))
//                .when(authService).register(any(RegisterRq.class));
//
//        mockMvc.perform(post("/auth/register"))
//                .andExpect(status().isConflict());
//    }

    @Test
    void refresh_shouldReturn200() throws Exception {
        RefreshOutput refreshOutput = new RefreshOutput(
                3L,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        TokenOutput tokenOutput = new TokenOutput("refreshed-access-token", "refreshed-refresh-token");

        when(refreshService.execute("raw-refresh-token")).thenReturn(refreshOutput);
        when(authService.generateToken(eq(3L), any())).thenReturn(tokenOutput);

        MvcResult mvcResult = mockMvc.perform(
                        post("/auth/refresh")
                                .cookie(new jakarta.servlet.http.Cookie("refresh_token", "raw-refresh-token"))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(header().exists("Set-Cookie"))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        String setCookie = mvcResult.getResponse().getHeader("Set-Cookie");

        assertTrue(jsonResponse.contains("refreshed-access-token"));
        assertTrue(setCookie != null && setCookie.contains("refreshed-refresh-token"));

        verify(refreshService).execute("raw-refresh-token");
        verify(authService).generateToken(eq(3L), any());
    }

//    @Test
//    void refresh_shouldReturn401() throws Exception {
//        doThrow(new UnauthorizedException("Refresh token missing"))
//                .when(refreshService).execute(any());
//
//        mockMvc.perform(post("/auth/refresh"))
//                .andExpect(status().isUnauthorized());
//    }
}