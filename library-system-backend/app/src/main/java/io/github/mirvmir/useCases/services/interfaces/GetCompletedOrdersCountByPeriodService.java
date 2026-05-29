package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.GetCompletedOrdersCountByPeriodInput;
import io.github.mirvmir.useCases.services.outputs.GetCompletedOrdersCountByPeriodOutput;

public interface GetCompletedOrdersCountByPeriodService {
    GetCompletedOrdersCountByPeriodOutput execute(GetCompletedOrdersCountByPeriodInput input);
}
