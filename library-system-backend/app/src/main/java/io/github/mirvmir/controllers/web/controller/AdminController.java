package io.github.mirvmir.controllers.web.controller;

import io.github.mirvmir.controllers.web.requests.GetOrderRq;
import io.github.mirvmir.controllers.web.requests.PathRq;
import io.github.mirvmir.controllers.web.requests.PeriodRq;
import io.github.mirvmir.useCases.services.inputs.*;
import io.github.mirvmir.useCases.services.interfaces.*;
import io.github.mirvmir.useCases.services.outputs.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CalculateTotalEarningsByPeriodService calculateTotalEarningsByPeriodService;
    private final GetCompletedOrdersCountByPeriodService getCompletedOrdersCountByPeriodService;
    private final CompleteOrderService completeOrderService;
    private final ExportOrderCsvService exportOrderCsvService;
    private final ImportOrderCsvService importOrderCsvService;
    private final GetOrderForAdminService getOrderForAdminService;
    private final CreateAdminService createAdminService;

    public AdminController(CalculateTotalEarningsByPeriodService calculateTotalEarningsByPeriodService,
                           GetCompletedOrdersCountByPeriodService getCompletedOrdersCountByPeriodService,
                           CompleteOrderService completeOrderService,
                           ExportOrderCsvService exportOrderCsvService,
                           ImportOrderCsvService importOrderCsvService,
                           GetOrderForAdminService getOrderForAdminService,
                           CreateAdminService createAdminService) {
        this.calculateTotalEarningsByPeriodService = calculateTotalEarningsByPeriodService;
        this.getCompletedOrdersCountByPeriodService = getCompletedOrdersCountByPeriodService;
        this.completeOrderService = completeOrderService;
        this.exportOrderCsvService = exportOrderCsvService;
        this.importOrderCsvService = importOrderCsvService;
        this.getOrderForAdminService = getOrderForAdminService;
        this.createAdminService = createAdminService;
    }

    @GetMapping("/earnings")
    public CalculateTotalEarningsByPeriodOutput earnings(PeriodRq rq) {
        CalculateTotalEarningsByPeriodInput input = new CalculateTotalEarningsByPeriodInput(
                rq.from(),
                rq.to()
        );
        return calculateTotalEarningsByPeriodService.execute(input);
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

        return getOrderForAdminService.execute(input);
    }

    @GetMapping("/orders/completed/count")
    public GetCompletedOrdersCountByPeriodOutput counts(PeriodRq rq) {
        GetCompletedOrdersCountByPeriodInput input = new GetCompletedOrdersCountByPeriodInput(
                rq.from(),
                rq.to()
        );
        return getCompletedOrdersCountByPeriodService.execute(input);
    }

    @PostMapping("/orders/{orderId}/complete")
    public CompleteOrderOutput complete(@PathVariable("orderId") Long orderId) {
        return completeOrderService.execute(new CompleteOrderInput(orderId));
    }

    @GetMapping("/orders/export")
    public ExportOrderOutput exportCsv(PathRq rq) {
        return exportOrderCsvService.execute(
                new ExportOrderCsvInput(rq.path())
        );
    }

    @PostMapping("/orders/import")
    public ImportOrderOutput importCsv(PathRq rq) {
        return importOrderCsvService.execute(
                new ImportOrderCsvInput(rq.path())
        );
    }

    @PostMapping("/assign-admin/{id}")
    public ResponseEntity<Void> assignAdmin(@PathVariable("id") Long id) {
        CreateAdminInput input = new CreateAdminInput(id);
        createAdminService.execute(input);
        return ResponseEntity.ok().build();
    }
}
