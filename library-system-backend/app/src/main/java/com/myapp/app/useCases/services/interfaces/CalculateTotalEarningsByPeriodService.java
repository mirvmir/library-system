package com.myapp.app.useCases.services.interfaces;

import com.myapp.app.useCases.services.inputs.CalculateTotalEarningsByPeriodInput;
import com.myapp.app.useCases.services.outputs.CalculateTotalEarningsByPeriodOutput;

public interface CalculateTotalEarningsByPeriodService {
    CalculateTotalEarningsByPeriodOutput execute(CalculateTotalEarningsByPeriodInput params);
}
