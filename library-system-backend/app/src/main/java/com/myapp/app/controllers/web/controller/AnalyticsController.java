package com.myapp.app.controllers.web.controller;

import com.myapp.app.controllers.requests.PeriodRq;
import com.myapp.app.useCases.services.inputs.*;
import com.myapp.app.useCases.services.interfaces.CalculateTotalEarningsByPeriodService;
import com.myapp.app.useCases.services.interfaces.GetCompletedOrdersCountByPeriodService;
import com.myapp.app.useCases.services.outputs.CalculateTotalEarningsByPeriodOutput;
import com.myapp.app.useCases.services.outputs.GetCompletedOrdersCountByPeriodOutput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final CalculateTotalEarningsByPeriodService calculateTotalEarningsByPeriodService;
    private final GetCompletedOrdersCountByPeriodService getCompletedOrdersCountByPeriodService;

    public AnalyticsController(CalculateTotalEarningsByPeriodService calculateTotalEarningsByPeriodService,
                               GetCompletedOrdersCountByPeriodService getCompletedOrdersCountByPeriodService) {
        this.calculateTotalEarningsByPeriodService = calculateTotalEarningsByPeriodService;
        this.getCompletedOrdersCountByPeriodService = getCompletedOrdersCountByPeriodService;
    }

    @GetMapping("/earnings")
    public CalculateTotalEarningsByPeriodOutput earnings(PeriodRq rq) {
        CalculateTotalEarningsByPeriodInput input = new CalculateTotalEarningsByPeriodInput(
                rq.from(),
                rq.to()
        );
        return calculateTotalEarningsByPeriodService.execute(input);
    }

    @GetMapping("/orders/completed/count")
    public GetCompletedOrdersCountByPeriodOutput counts(PeriodRq rq) {
        GetCompletedOrdersCountByPeriodInput input = new GetCompletedOrdersCountByPeriodInput(
                rq.from(),
                rq.to()
        );
        return getCompletedOrdersCountByPeriodService.execute(input);
    }
}
