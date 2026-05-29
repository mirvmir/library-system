package io.github.mirvmir.controllers.web.controller;

import io.github.mirvmir.controllers.web.requests.GetOrderRq;
import io.github.mirvmir.useCases.services.inputs.CancelOrderInput;
import io.github.mirvmir.useCases.services.inputs.CreateOrderInput;
import io.github.mirvmir.useCases.services.inputs.GetOrderDescriptionInput;
import io.github.mirvmir.useCases.services.inputs.GetOrderInput;
import io.github.mirvmir.useCases.services.interfaces.*;
import io.github.mirvmir.useCases.services.outputs.CancelOrderOutput;
import io.github.mirvmir.useCases.services.outputs.CreateOrderOutput;
import io.github.mirvmir.useCases.services.outputs.GetOrdersOutput;
import io.github.mirvmir.useCases.services.outputs.OrderDescriptionOutput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@PreAuthorize("hasAuthority('ROLE_USER')")
public class OrderController {

    private final GetOrderService getOrderService;
    private final GetOrderDescriptionService getOrderDescriptionService;
    private final CreateOrderService createOrderService;
    private final CreateOrderFromBasketService createOrderFromBasketService;
    private final CancelOrderService cancelOrderService;

    public OrderController(GetOrderService getOrderService,
                           GetOrderDescriptionService getOrderDescriptionService,
                           CreateOrderService createOrderService,
                           CreateOrderFromBasketService createOrderFromBasketService,
                           CancelOrderService cancelOrderService) {
        this.getOrderService = getOrderService;
        this.getOrderDescriptionService = getOrderDescriptionService;
        this.createOrderService = createOrderService;
        this.createOrderFromBasketService = createOrderFromBasketService;
        this.cancelOrderService = cancelOrderService;
    }

    @GetMapping
    public GetOrdersOutput getAll(GetOrderRq rq) {
        GetOrderInput input = new GetOrderInput(
                rq.type().toString(),
                rq.filtered(),
                rq.direction().toString(),
                rq.field().toString(),
                rq.from(), rq.to()
        );

        return getOrderService.execute(input);
    }

    @GetMapping("/{orderId}")
    public OrderDescriptionOutput getById(@PathVariable("orderId") Long orderId) {
        return getOrderDescriptionService.execute(new GetOrderDescriptionInput(orderId));
    }

    @PostMapping("/{bookIsbn}")
    public CreateOrderOutput createOrder(@PathVariable("bookIsbn") String bookIsbn) {
        CreateOrderInput input = new CreateOrderInput(
                bookIsbn
        );

        return createOrderService.execute(input);
    }

    @PostMapping
    public CreateOrderOutput createOrderFromBasket() {
        return createOrderFromBasketService.execute();
    }

    @PostMapping("/{orderId}/cancel")
    public CancelOrderOutput cancel(@PathVariable("orderId") Long orderId) {
        return cancelOrderService.execute(new CancelOrderInput(orderId));
    }
}
