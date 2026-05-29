package io.github.mirvmir.useCases.services.interfaces;

import io.github.mirvmir.useCases.services.inputs.CalculateTotalEarningsByPeriodInput;
import io.github.mirvmir.useCases.services.outputs.CalculateTotalEarningsByPeriodOutput;

public interface CalculateTotalEarningsByPeriodService {
    CalculateTotalEarningsByPeriodOutput execute(CalculateTotalEarningsByPeriodInput params);
}
