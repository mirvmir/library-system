package io.github.mirvmir.controllers.controller;

import io.github.mirvmir.config.TestConfig;
import io.github.mirvmir.config.WebConfig;
import io.github.mirvmir.exception.business.IncompatibleSortTypesException;
import io.github.mirvmir.exception.business.InvalidPathException;
import io.github.mirvmir.exception.business.OrderImportException;
import io.github.mirvmir.exception.business.OrderNotFoundException;
import io.github.mirvmir.useCases.services.inputs.*;
import io.github.mirvmir.useCases.services.interfaces.*;
import io.github.mirvmir.useCases.services.outputs.*;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
class OrderControllerTest {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private GetOrderService getOrderService;
    @Autowired
    private GetOrderDescriptionService getOrderDescriptionService;
    @Autowired
    private CreateOrderService createOrderService;
    @Autowired
    private CancelOrderService cancelOrderService;
    @Autowired
    private CompleteOrderService completeOrderService;
    @Autowired
    private ExportOrderCsvService exportOrderCsvService;
    @Autowired
    private ImportOrderCsvService importOrderCsvService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reset(
                getOrderService,
                getOrderDescriptionService,
                createOrderService,
                cancelOrderService,
                completeOrderService,
                exportOrderCsvService,
                importOrderCsvService
        );

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void getAll_shouldReturn200() throws Exception {
        GetOrdersOutput output = new GetOrdersOutput(List.of(
                new GetOrderOutput(
                        1L,
                        2L,
                        "NEW",
                        LocalDateTime.of(2026, 4, 4, 0, 0),
                        BigDecimal.valueOf(1230),
                        List.of("123", "321"))
        ));

        when(getOrderService.execute(any())).thenReturn(output);

        mockMvc.perform(
                        get("/orders")
                                .queryParam("type", "COMPLETED_ORDER")
                                .queryParam("filtered", "true")
                                .queryParam("from", "2024-04-04T00:00:00")
                                .queryParam("to", "2026-04-04T00:00:00")
                                .queryParam("direction", "ASC")
                                .queryParam("field", "COMPLETION_DATE")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        verify(getOrderService).execute(argThat(request ->
                request != null
                        && request.type().equals("COMPLETED_ORDER")
                        && request.filtered()
                        && request.from().equals(LocalDateTime.of(2024, 4, 4, 0, 0))
                        && request.to().equals(LocalDateTime.of(2026, 4, 4, 0, 0))
                        && request.direction().equals("ASC")
                        && request.field().equals("COMPLETION_DATE")
        ));
    }

    @Test
    void getAll_shouldReturn400() throws Exception {
        doThrow(new IncompatibleSortTypesException("Incompatible types for sorting: COMPLETED_ORDER and AVAILABILITY."))
                .when(getOrderService).execute(any(GetOrderInput.class));

        mockMvc.perform(get("/orders")
                        .queryParam("type", "COMPLETED_ORDER")
                        .queryParam("filtered", "true")
                        .queryParam("from", "2024-04-04T10:00:00")
                        .queryParam("to", "2026-04-04T10:00:00")
                        .queryParam("direction", "ASC")
                        .queryParam("field", "COMPLETION_DATE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_shouldReturn200() throws Exception {
        OrderDescriptionOutput output = new OrderDescriptionOutput(
                1L,
                2L,
                "NEW",
                null,
                LocalDateTime.of(2026, 4, 4, 0, 0),
                BigDecimal.valueOf(1230),
                List.of("123", "321")
        );

        when(getOrderDescriptionService.execute(any())).thenReturn(output);

        mockMvc.perform(
                        get("/orders/1")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        verify(getOrderDescriptionService).execute(argThat(request ->
                request != null
                        && request.orderId().equals(1L)
        ));
    }

    @Test
    void getById_shouldReturn404() throws Exception {
        doThrow(new OrderNotFoundException("Order with ID 1 not found."))
                .when(getOrderDescriptionService).execute(any(GetOrderDescriptionInput.class));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isNotFound());
    }

//    @Test
//    void createOrder_shouldReturn404() throws Exception {
//        doThrow(new OrderNotFoundException("Order with ID 1 not found."))
//                .when(getOrderDescriptionService).execute(any(GetOrderDescriptionInput.class));
//
//        mockMvc.perform(
//                post("/orders")
//                        .contentType(APPLICATION_JSON).content("""
//                                        {
//                                          "listIsbn": ["123", "321"],
//                                          "customerId": "1"
//                                        }
//                                        """))
//                .andExpect(status().isNotFound());
//    }

    @Test
    void cancelOrder_shouldReturn200() throws Exception {
        when(cancelOrderService.execute(any())).thenReturn(new CancelOrderOutput());

        mockMvc.perform(
                        post("/orders/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        verify(cancelOrderService).execute(argThat(request ->
                request != null
                        && request.orderId().equals(1L)
        ));
    }

    @Test
    void cancelOrder_shouldReturn404() throws Exception {
        doThrow(new OrderNotFoundException("Order with ID 1 not found."))
                .when(cancelOrderService).execute(any(CancelOrderInput.class));

        mockMvc.perform(post("/orders/1/cancel"))
                .andExpect(status().isNotFound());
    }

    @Test
    void completeOrder_shouldReturn200() throws Exception {
        when(completeOrderService.execute(any())).thenReturn(new CompleteOrderOutput());

        mockMvc.perform(
                        post("/orders/1/complete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andReturn();

        verify(completeOrderService).execute(argThat(request ->
                request != null
                        && request.orderId().equals(1L)
        ));
    }

    @Test
    void completeOrder_shouldReturn404() throws Exception {
        doThrow(new OrderNotFoundException("Order with ID 1 not found."))
                .when(completeOrderService).execute(any(CompleteOrderInput.class));

        mockMvc.perform(post("/orders/1/complete"))
                .andExpect(status().isNotFound());
    }

    @Test
    void exportCsv_shouldReturn200() throws Exception {
        ExportOrderOutput output = new ExportOrderOutput();

        when(exportOrderCsvService.execute(any(ExportOrderCsvInput.class)))
                .thenReturn(output);

        mockMvc.perform(get("/orders/export")
                        .param("path", "/tmp/orders.csv"))
                .andExpect(status().isOk());

        verify(exportOrderCsvService).execute(argThat(input ->
                input.path().equals("/tmp/orders.csv")
        ));
    }

    @Test
    void exportCsv_shouldReturn400_whenPathInvalid() throws Exception {
        when(exportOrderCsvService.execute(any(ExportOrderCsvInput.class)))
                .thenThrow(new InvalidPathException("Invalid path: /tmp/test.csv."));

        mockMvc.perform(get("/orders/export")
                        .param("path", "/tmp/test.csv"))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void exportCsv_shouldReturn500_whenWriteFailed() throws Exception {
//        when(exportOrderCsvService.execute(any(ExportOrderCsvInput.class)))
//                .thenThrow(new IllegalStateException("Ошибка записи файла."));
//
//        mockMvc.perform(get("/export")
//                        .param("path", "/tmp/test.csv"))
//                .andExpect(status().isInternalServerError());
//    }

    @Test
    void importCsv_shouldReturn200() throws Exception {
        ImportOrderOutput output = new ImportOrderOutput();

        when(importOrderCsvService.execute(any(ImportOrderCsvInput.class)))
                .thenReturn(output);

        mockMvc.perform(post("/orders/import")
                        .param("path", "/tmp/orders.csv"))
                .andExpect(status().isOk());

        verify(importOrderCsvService).execute(argThat(input ->
                input.path().equals("/tmp/orders.csv")
        ));
    }

    @Test
    void importCsv_shouldReturn400_whenPathInvalid() throws Exception {
        when(importOrderCsvService.execute(any(ImportOrderCsvInput.class)))
                .thenThrow(new InvalidPathException("Invalid path: /tmp/test.csv."));

        mockMvc.perform(post("/orders/import")
                        .param("path", "/tmp/test.csv"))
                .andExpect(status().isBadRequest());
    }

//    @Test
//    void importCsv_shouldReturn500_whenReadFailed() throws Exception {
//        when(importOrderCsvService.execute(any(ImportOrderCsvInput.class)))
//                .thenThrow(new IllegalStateException("Ошибка чтения файла."));
//
//        mockMvc.perform(post("/orders/import")
//                        .param("path", "/tmp/test.csv"))
//                .andExpect(status().isInternalServerError());
//    }

    @Test
    void importCsv_shouldReturn400_whenCustomerNotFound() throws Exception {
        when(importOrderCsvService.execute(any(ImportOrderCsvInput.class)))
                .thenThrow(new OrderImportException("Покупатель с ID 1 не найден."));

        mockMvc.perform(post("/orders/import")
                        .param("path", "/tmp/test.csv"))
                .andExpect(status().isBadRequest());
    }
}