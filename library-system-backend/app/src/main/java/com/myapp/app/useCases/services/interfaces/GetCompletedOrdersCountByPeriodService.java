package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.GetCompletedOrdersCountByPeriodInput;
import com.myapp.app.useCases.services.outputs.GetCompletedOrdersCountByPeriodOutput;

public interface GetCompletedOrdersCountByPeriodService {
    GetCompletedOrdersCountByPeriodOutput execute(GetCompletedOrdersCountByPeriodInput input);
}
