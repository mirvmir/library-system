//package io.github.mirvmir.useCases.services;
//
//import io.github.mirvmir.domain.entities.order.Order;
//import io.github.mirvmir.domain.entities.order.OrderItem;
//import io.github.mirvmir.domain.entities.order.OrderStatus;
//import io.github.mirvmir.domain.entities.user.Role;
//import io.github.mirvmir.domain.entities.user.User;
//import io.github.mirvmir.exception.business.OrderNotFoundException;
//import io.github.mirvmir.useCases.adapter.repository.interfaces.OrderRepository;
//import io.github.mirvmir.useCases.adapter.repository.interfaces.UserRepository;
//import io.github.mirvmir.useCases.services.implementation.DefaultGetOrderDescriptionService;
//import io.github.mirvmir.useCases.services.inputs.GetOrderDescriptionInput;
//import io.github.mirvmir.useCases.services.outputs.OrderDescriptionOutput;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class DefaultGetOrderDescriptionServiceTest {
//    private OrderRepository orderRepo;
//    private UserRepository userRepo;
//
//    private DefaultGetOrderDescriptionService service;
//
//    @BeforeEach
//    void setUp() {
//        orderRepo = mock(OrderRepository.class);
//        userRepo = mock(UserRepository.class);
//
//        service = new DefaultGetOrderDescriptionService(orderRepo);
//    }
//
//    @Test
//    void execute_shouldReturnOrderDescription() {
//        GetOrderDescriptionInput input = new GetOrderDescriptionInput(1L);
//
//        List<OrderItem> items = List.of(
//                new OrderItem(2L, "123"),
//                new OrderItem(3L, "321")
//        );
//        Long customerId = 4L;
//        Order orderForGetDescription = new Order(
//                customerId,
//                items,
//                OrderStatus.CANCELLED,
//                LocalDateTime.of(2025, 12, 12, 0, 0),
//                new BigDecimal("700"),
//                null
//        );
//        orderForGetDescription.setId(1L);
//
//        User user = mock(User.class);
//        when(user.getId())
//                .thenReturn(customerId);
//        when(user.getRole())
//                .thenReturn(Role.USER);
//        when(user.getPasswordHash())
//                .thenReturn("password_hash");
//        when(user.getEmail())
//                .thenReturn("user@example.com");
//
//        when(orderRepo.findById(input.orderId())).thenReturn(orderForGetDescription);
//        when(userRepo.findById(customerId)).thenReturn(user);
//
//        OrderDescriptionOutput result = service.execute(input);
//
//        assertEquals(orderForGetDescription.getId(), result.id());
//        assertEquals(orderForGetDescription.getCustomerId(), result.customerId());
//        assertEquals(orderForGetDescription.getStatus().toString(), result.status());
//        assertEquals(orderForGetDescription.getCompletionDate(), result.completionDate());
//        assertEquals(orderForGetDescription.getCreatedDate(), result.createdDate());
//        assertEquals(orderForGetDescription.getTotalPrice(), result.totalPrice());
//        assertEquals(List.of("123", "321"), result.isbns());
//
//        verify(orderRepo).findById(input.orderId());
//        verify(userRepo).findById(orderForGetDescription.getCustomerId());
//    }
//
//    @Test
//    void execute_shouldNotReturnOrderDescription_shouldThrownException_whenOrderNotFound() {
//        GetOrderDescriptionInput input = new GetOrderDescriptionInput(1L);
//        when(orderRepo.findById(input.orderId()))
//                .thenReturn(null);
//
//        OrderNotFoundException exception = assertThrows(
//                OrderNotFoundException.class,
//                () -> service.execute(input)
//        );
//        assertEquals("Order with ID " + input.orderId() + " not found.", exception.getMessage());
//
//        verify(orderRepo).findById(input.orderId());
//        verify(userRepo, never()).findById(anyLong());
//    }
//}
