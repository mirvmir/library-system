package io.github.mirvmir.controllers.controller;

import io.github.mirvmir.config.TestConfig;
import io.github.mirvmir.config.WebConfig;
import io.github.mirvmir.useCases.services.inputs.CalculateTotalEarningsByPeriodInput;
import io.github.mirvmir.useCases.services.inputs.CreateAdminInput;
import io.github.mirvmir.useCases.services.inputs.GetCompletedOrdersCountByPeriodInput;
import io.github.mirvmir.useCases.services.interfaces.CalculateTotalEarningsByPeriodService;
import io.github.mirvmir.useCases.services.interfaces.CreateAdminService;
import io.github.mirvmir.useCases.services.interfaces.GetCompletedOrdersCountByPeriodService;
import io.github.mirvmir.useCases.services.outputs.CalculateTotalEarningsByPeriodOutput;
import io.github.mirvmir.useCases.services.outputs.GetCompletedOrdersCountByPeriodOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        WebConfig.class,
        TestConfig.class
})
@WebAppConfiguration
public class AdminControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private CalculateTotalEarningsByPeriodService calculateTotalEarningsByPeriodService;
    @Autowired
    private GetCompletedOrdersCountByPeriodService getCompletedOrdersCountByPeriodService;
    @Autowired
    private CreateAdminService createAdminService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(
                calculateTotalEarningsByPeriodService,
                getCompletedOrdersCountByPeriodService,
                createAdminService
        );

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void earnings_shouldReturn200() throws Exception {
        CalculateTotalEarningsByPeriodOutput output =
                new CalculateTotalEarningsByPeriodOutput(BigDecimal.valueOf(1230));

        when(calculateTotalEarningsByPeriodService.execute(any(CalculateTotalEarningsByPeriodInput.class)))
                .thenReturn(output);

        MvcResult mvcResult = mockMvc.perform(
                        get("/admin/earnings")
                                .queryParam("from", "2024-04-04T00:00:00")
                                .queryParam("to", "2026-04-04T00:00:00")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertTrue(jsonResponse.contains("1230"));

        verify(calculateTotalEarningsByPeriodService).execute(argThat(input ->
                input != null
                        && input.from().equals(LocalDateTime.of(2024, 4, 4, 0, 0))
                        && input.to().equals(LocalDateTime.of(2026, 4, 4, 0, 0))
        ));
    }

    @Test
    void counts_shouldReturn200() throws Exception {
        GetCompletedOrdersCountByPeriodOutput output =
                new GetCompletedOrdersCountByPeriodOutput(7L);

        when(getCompletedOrdersCountByPeriodService.execute(any(GetCompletedOrdersCountByPeriodInput.class)))
                .thenReturn(output);

        MvcResult mvcResult = mockMvc.perform(
                        get("/admin/orders/completed/count")
                                .queryParam("from", "2024-04-04T00:00:00")
                                .queryParam("to", "2026-04-04T00:00:00")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();

        assertTrue(jsonResponse.contains("7"));

        verify(getCompletedOrdersCountByPeriodService).execute(argThat(input ->
                input != null
                        && input.from().equals(LocalDateTime.of(2024, 4, 4, 0, 0))
                        && input.to().equals(LocalDateTime.of(2026, 4, 4, 0, 0))
        ));
    }

    @Test
    void assignAdmin_shouldReturn200() throws Exception {
        doNothing().when(createAdminService).execute(any(CreateAdminInput.class));

        mockMvc.perform(post("/admin/assign-admin/5"))
                .andExpect(status().isOk());

        verify(createAdminService).execute(argThat(input ->
                input != null && input.userId().equals(5L)
        ));
    }

//    @Test
//    void assignAdmin_shouldReturn401() throws Exception {
//        doThrow(new EntityNotFoundException("Entity not found."))
//                .when(createAdminService).execute(any(CreateAdminInput.class));
//
//        mockMvc.perform(post("/admin/assign-admin/5"))
//                .andExpect(status().isNotFound());
//    }
}