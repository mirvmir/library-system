package io.github.mirvmir.controllers.controller;

import io.github.mirvmir.config.TestConfig;
import io.github.mirvmir.config.WebConfig;
import io.github.mirvmir.exception.business.IncompatibleSortTypesException;
import io.github.mirvmir.useCases.services.inputs.GetBookRequestInput;
import io.github.mirvmir.useCases.services.interfaces.GetBookRequestService;
import io.github.mirvmir.useCases.services.outputs.GetBookRequestOutput;
import io.github.mirvmir.useCases.services.outputs.GetBookRequestsOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        WebConfig.class,
        TestConfig.class
})
@WebAppConfiguration
public class BookRequestControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private GetBookRequestService getBookRequestService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(
                getBookRequestService
        );

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void getAll_shouldReturn200() throws Exception {
        GetBookRequestsOutput output = new GetBookRequestsOutput(List.of(
                new GetBookRequestOutput(
                        "123",
                        "Преступление и наказание",
                        "Достоевский",
                        "321",
                        true,
                        3))
        );

        when(getBookRequestService.execute(any())).thenReturn(output);

        mockMvc.perform(
                        get("/requests")
                                .queryParam("type", "REQUEST")
                                .queryParam("direction", "ASC")
                                .queryParam("field", "COUNT")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        verify(getBookRequestService).execute(argThat(request ->
                request != null
                        && request.type().equals("REQUEST")
                        && request.direction().equals("ASC")
                        && request.field().equals("COUNT")
        ));
    }

    @Test
    void getAll_shouldReturn400() throws Exception {
        doThrow(new IncompatibleSortTypesException("Incompatible types for sorting: REQUEST and PRICE."))
                .when(getBookRequestService).execute(any(GetBookRequestInput.class));

        mockMvc.perform(get("/requests")
                        .queryParam("type", "REQUEST")
                        .queryParam("direction", "ASC")
                        .queryParam("field", "COUNT"))
                .andExpect(status().isBadRequest());
    }
}
