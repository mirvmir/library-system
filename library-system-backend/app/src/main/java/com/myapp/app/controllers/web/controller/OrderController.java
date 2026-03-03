package com.myapp.app.controllers.web.controller;

import com.myapp.app.controllers.requests.GetOrderRq;
import com.myapp.app.controllers.requests.OrderRq;
import com.myapp.app.controllers.requests.PathRq;
import com.myapp.app.useCases.services.inputs.*;
import com.myapp.app.useCases.services.interfaces.*;
import com.myapp.app.useCases.services.outputs.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final GetOrderService getOrderService;
    private final GetOrderDescriptionService getOrderDescriptionService;
    private final CreateOrderService createOrderService;
    private final CancelOrderService cancelOrderService;
    private final CompleteOrderService completeOrderService;
    private final ExportOrderCsvService exportOrderCsvService;
    private final ImportOrderCsvService importOrderCsvService;

    public OrderController(GetOrderService getOrderService,
                           GetOrderDescriptionService getOrderDescriptionService,
                           CreateOrderService createOrderService,
                           CancelOrderService cancelOrderService,
                           CompleteOrderService completeOrderService,
                           ExportOrderCsvService exportOrderCsvService,
                           ImportOrderCsvService importOrderCsvService) {
        this.getOrderService = getOrderService;
        this.getOrderDescriptionService = getOrderDescriptionService;
        this.createOrderService = createOrderService;
        this.cancelOrderService = cancelOrderService;
        this.completeOrderService = completeOrderService;
        this.exportOrderCsvService = exportOrderCsvService;
        this.importOrderCsvService = importOrderCsvService;
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
    public OrderDescriptionOutput getById(@PathVariable Long orderId) {
        return getOrderDescriptionService.execute(new GetOrderDescriptionInput(orderId));
    }

    @PostMapping
    public CreateOrderOutput createOrder(@RequestBody OrderRq rq) {
        CreateOrderInput input = new CreateOrderInput(
                rq.customerId(),
                rq.listIsbn()
        );

        return createOrderService.execute(input);
    }

    @PostMapping("/{orderId}/cancel")
    public CancelOrderOutput cancel(@PathVariable Long orderId) {
        return cancelOrderService.execute(new CancelOrderInput(orderId));
    }

    @PostMapping("/{orderId}/complete")
    public CompleteOrderOutput complete(@PathVariable Long orderId) {
        return completeOrderService.execute(new CompleteOrderInput(orderId));
    }

    @GetMapping("/export")
    public ExportOrderOutput exportCsv(PathRq rq) {
        return exportOrderCsvService.execute(
                new ExportOrderCsvInput(rq.path())
        );
    }

    @PostMapping("/import")
    public ImportOrderOutput importCsv(PathRq rq) {
        return importOrderCsvService.execute(
                new ImportOrderCsvInput(rq.path())
        );
    }
}
