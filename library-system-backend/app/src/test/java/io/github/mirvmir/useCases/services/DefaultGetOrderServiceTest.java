//package io.github.mirvmir.useCases.services;
//
//import io.github.mirvmir.domain.entities.order.Order;
//import io.github.mirvmir.domain.entities.order.OrderItem;
//import io.github.mirvmir.domain.entities.order.OrderStatus;
//import io.github.mirvmir.exception.business.IncompatibleSortTypesException;
//import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
//import io.github.mirvmir.useCases.services.implementation.DefaultGetOrderService;
//import io.github.mirvmir.useCases.services.inputs.GetOrderInput;
//import io.github.mirvmir.useCases.services.outputs.GetOrdersOutput;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//public class DefaultGetOrderServiceTest {
//    private OrderRepository orderRepo;
//    private DefaultGetOrderService service;
//
//    private List<OrderItem> emptyItems;
//    private List<OrderItem> items4;
//
//    private Order order1;
//    private Order order2;
//    private Order order3;
//    private Order order4;
//    private Order order5;
//    private Order order6;
//
//    private List<Order> allOrders;
//
//    @BeforeEach
//    void setUp() {
//        orderRepo = mock(OrderRepository.class);
//        service = new DefaultGetOrderService(orderRepo);
//
//        emptyItems = List.of();
//        items4 = List.of(new OrderItem(7L, "111"));
//
//        order1 = new Order(7L, emptyItems, OrderStatus.CANCELLED,
//                LocalDateTime.of(2024, 4, 5, 0, 0),
//                new BigDecimal("500"), null);
//        order1.setId(1L);
//
//        order2 = new Order(7L, emptyItems, OrderStatus.COMPLETED,
//                LocalDateTime.of(2024, 4, 3, 0, 0),
//                new BigDecimal("100"),
//                LocalDateTime.of(2024, 4, 4, 0, 0));
//        order2.setId(2L);
//
//        order3 = new Order(7L, emptyItems, OrderStatus.COMPLETED,
//                LocalDateTime.of(2023, 4, 3, 0, 0),
//                new BigDecimal("700"),
//                LocalDateTime.of(2023, 4, 4, 0, 0));
//        order3.setId(3L);
//
//        order4 = new Order(7L, items4, OrderStatus.COMPLETED,
//                LocalDateTime.of(2024, 4, 4, 0, 0),
//                new BigDecimal("100"),
//                LocalDateTime.of(2025, 4, 4, 0, 0));
//        order4.setId(4L);
//
//        order5 = new Order(7L, emptyItems, OrderStatus.NEW,
//                LocalDateTime.of(2024, 4, 5, 0, 0),
//                new BigDecimal("700"), null);
//        order5.setId(5L);
//
//        order6 = new Order(7L, emptyItems, OrderStatus.COMPLETED,
//                LocalDateTime.of(2023, 4, 5, 0, 0),
//                new BigDecimal("700"),
//                LocalDateTime.of(2026, 5, 5, 0, 0));
//        order6.setId(6L);
//
//        allOrders = List.of(order1, order2, order3, order4, order5, order6);
//    }
//
//    @Test
//    void execute_shouldReturnOrders_sortedByCompletionDateAsc() {
//        GetOrderInput input = new GetOrderInput("ORDER", false, "ASC", "COMPLETION_DATE",
//                null, null);
//
//        when(orderRepo.findAll()).thenReturn(allOrders);
//
//        GetOrdersOutput result = service.execute(input);
//
//        assertEquals(6, result.orders().size());
//        assertEquals(3L, result.orders().get(0).id());
//        assertEquals(2L, result.orders().get(1).id());
//        assertEquals(4L, result.orders().get(2).id());
//        assertEquals(6L, result.orders().get(3).id());
//        assertEquals(1L, result.orders().get(4).id());
//        assertEquals(5L, result.orders().get(5).id());
//
//        verify(orderRepo).findAll();
//    }
//
//    @Test
//    void execute_shouldReturnOrders_sortedByCompletionDateDesc() {
//        GetOrderInput input = new GetOrderInput("ORDER", false, "DESC", "COMPLETION_DATE",
//                null, null);
//
//        when(orderRepo.findAll()).thenReturn(allOrders);
//
//        GetOrdersOutput result = service.execute(input);
//
//        assertEquals(6, result.orders().size());
//        assertEquals(5L, result.orders().get(0).id());
//        assertEquals(1L, result.orders().get(1).id());
//        assertEquals(6L, result.orders().get(2).id());
//        assertEquals(4L, result.orders().get(3).id());
//        assertEquals(2L, result.orders().get(4).id());
//        assertEquals(3L, result.orders().get(5).id());
//
//        verify(orderRepo).findAll();
//    }
//
//    @Test
//    void execute_shouldReturnOrders_sortedByPriceAsc() {
//        GetOrderInput input = new GetOrderInput("ORDER", false, "ASC", "PRICE",
//                null, null);
//
//        when(orderRepo.findAll()).thenReturn(allOrders);
//
//        GetOrdersOutput result = service.execute(input);
//
//        assertEquals(6, result.orders().size());
//        assertEquals(2L, result.orders().get(0).id());
//        assertEquals(4L, result.orders().get(1).id());
//        assertEquals(1L, result.orders().get(2).id());
//        assertEquals(3L, result.orders().get(3).id());
//        assertEquals(5L, result.orders().get(4).id());
//        assertEquals(6L, result.orders().get(5).id());
//
//        verify(orderRepo).findAll();
//    }
//
//    @Test
//    void execute_shouldReturnOrders_sortedByPriceDesc() {
//        GetOrderInput input = new GetOrderInput("ORDER", false, "DESC", "PRICE",
//                null, null);
//
//        when(orderRepo.findAll()).thenReturn(allOrders);
//
//        GetOrdersOutput result = service.execute(input);
//
//        assertEquals(6, result.orders().size());
//        assertEquals(3L, result.orders().get(0).id());
//        assertEquals(5L, result.orders().get(1).id());
//        assertEquals(6L, result.orders().get(2).id());
//        assertEquals(1L, result.orders().get(3).id());
//        assertEquals(2L, result.orders().get(4).id());
//        assertEquals(4L, result.orders().get(5).id());
//
//        verify(orderRepo).findAll();
//    }
//
//    @Test
//    void execute_shouldReturnOrders_sortedByStatusAsc() {
//        GetOrderInput input = new GetOrderInput("ORDER", false, "ASC", "STATUS",
//                null, null);
//
//        when(orderRepo.findAll()).thenReturn(allOrders);
//
//        GetOrdersOutput result = service.execute(input);
//
//        assertEquals(6, result.orders().size());
//        assertEquals(5L, result.orders().get(0).id());
//        assertEquals(2L, result.orders().get(1).id());
//        assertEquals(3L, result.orders().get(2).id());
//        assertEquals(4L, result.orders().get(3).id());
//        assertEquals(6L, result.orders().get(4).id());
//        assertEquals(1L, result.orders().get(5).id());
//
//        verify(orderRepo).findAll();
//    }
//
//    @Test
//    void execute_shouldReturnOrders_sortedByStatusDesc() {
//        GetOrderInput input = new GetOrderInput("ORDER", false, "DESC", "STATUS",
//                null, null);
//
//        when(orderRepo.findAll()).thenReturn(allOrders);
//
//        GetOrdersOutput result = service.execute(input);
//
//        assertEquals(6, result.orders().size());
//        assertEquals(1L, result.orders().get(0).id());
//        assertEquals(2L, result.orders().get(1).id());
//        assertEquals(3L, result.orders().get(2).id());
//        assertEquals(4L, result.orders().get(3).id());
//        assertEquals(6L, result.orders().get(4).id());
//        assertEquals(5L, result.orders().get(5).id());
//
//        verify(orderRepo).findAll();
//    }
//
//    @Test
//    void execute_shouldReturnCompletedOrders_sortedByCompletionDateAsc() {
//        GetOrderInput input = new GetOrderInput("COMPLETED_ORDER", false, "ASC",
//                "COMPLETION_DATE", null, null);
//
//        when(orderRepo.findAll()).thenReturn(allOrders);
//
//        GetOrdersOutput result = service.execute(input);
//
//        assertEquals(4, result.orders().size());
//        assertEquals(3L, result.orders().get(0).id());
//        assertEquals(2L, result.orders().get(1).id());
//        assertEquals(4L, result.orders().get(2).id());
//        assertEquals(6L, result.orders().get(3).id());
//
//        verify(orderRepo).findAll();
//    }
//
//    @Test
//    void execute_shouldReturnCompletedOrders_sortedByCompletionDateDesc() {
//        GetOrderInput input = new GetOrderInput("COMPLETED_ORDER", false, "DESC",
//                "COMPLETION_DATE", null, null);
//
//        when(orderRepo.findAll()).thenReturn(allOrders);
//
//        GetOrdersOutput result = service.execute(input);
//
//        assertEquals(4, result.orders().size());
//        assertEquals(6L, result.orders().get(0).id());
//        assertEquals(4L, result.orders().get(1).id());
//        assertEquals(2L, result.orders().get(2).id());
//        assertEquals(3L, result.orders().get(3).id());
//
//        verify(orderRepo).findAll();
//    }
//
//    @Test
//    void execute_shouldReturnCompletedOrders_sortedByPriceAsc() {
//        GetOrderInput input = new GetOrderInput("COMPLETED_ORDER", false, "ASC",
//                "PRICE", null, null);
//
//        when(orderRepo.findAll()).thenReturn(allOrders);
//
//        GetOrdersOutput result = service.execute(input);
//
//        assertEquals(4, result.orders().size());
//        assertEquals(2L, result.orders().get(0).id());
//        assertEquals(4L, result.orders().get(1).id());
//        assertEquals(3L, result.orders().get(2).id());
//        assertEquals(6L, result.orders().get(3).id());
//
//        verify(orderRepo).findAll();
//    }
//
//    @Test
//    void execute_shouldReturnCompletedOrders_sortedByPriceDesc() {
//        GetOrderInput input = new GetOrderInput("COMPLETED_ORDER", false, "DESC",
//                "PRICE", null, null);
//
//        when(orderRepo.findAll()).thenReturn(allOrders);
//
//        GetOrdersOutput result = service.execute(input);
//
//        assertEquals(4, result.orders().size());
//        assertEquals(3L, result.orders().get(0).id());
//        assertEquals(6L, result.orders().get(1).id());
//        assertEquals(2L, result.orders().get(2).id());
//        assertEquals(4L, result.orders().get(3).id());
//
//        verify(orderRepo).findAll();
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//            "REQUEST,PRICE,ASC",
//            "REQUEST,AUTHOR,DESC",
//            "ORDER,TITLE,ASC",
//            "BOOK,DELIVERY_DATE,ASC",
//            "STALE_BOOK,TITLE,DESC",
//            "COMPLETED_ORDER,TITLE,ASC"
//    })
//    void execute_shouldNotReturnRequestedBooks_shouldThrowException_whenSortTypesAreIncompatible(
//            String type,
//            String field,
//            String direction) {
//        GetOrderInput input = new GetOrderInput(type,  false, direction, field, null, null);
//
//        assertThrows(IncompatibleSortTypesException.class,
//                () -> service.execute(input)
//        );
//
//        verify(orderRepo, never()).findAll();
//    }
//}